package com.quectel.camera2test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by klein on 18-5-9.
 */

public class Camera2ImageReaderTestFragment extends Fragment {
    public static final String TAG = "C2PTF";

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private TextureView mTextureView;

    private String mCameraId;
    private CameraDevice mCameraDevice;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private ImageReader mImageReader;
    private HandlerThread mThreadHandler;
    private Handler mHandler;

    // 很多过程都变成了异步的了，所以这里需要一个子线程的looper
    private void initLooper() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            //size of this TextureView = widthxheight
            Log.d(TAG, "--mTextureView onSurfaceTextureAvailable-- width = "
                    + width + ", height = " + height);
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "--mTextureView onSurfaceTextureSizeChanged--");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            Log.d(TAG, "--mTextureView onSurfaceTextureDestroyed--");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {

            Log.d(TAG, "--mTextureView onSurfaceTextureUpdated--");
            Bitmap bitmap= mTextureView.getBitmap();
            Log.d(TAG, "--mTextureView bitmap = " + bitmap.getByteCount());
        }

    };

    private final CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraDevice = cameraDevice;
            try {
                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                assert texture != null;

                // We configure the size of default buffer to be the size of camera preview we want.
                texture.setDefaultBufferSize(720, 1280);

                // This is the output Surface we need to start preview.
                Surface surface = new Surface(texture);

                // We set up a CaptureRequest.Builder with the output Surface.
                mPreviewRequestBuilder
                        = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);

                // Here, we create a CameraCaptureSession for camera preview.
                mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                        mCaptureStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    private final CameraCaptureSession.StateCallback mCaptureStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            // The camera is already closed
            if (null == mCameraDevice) {
                return;
            }

            // When the session is ready, we start displaying the preview.
            mCaptureSession = cameraCaptureSession;
            try {
                // Auto focus should be continuous for camera preview.
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // Flash is automatically enabled when necessary.

                // Finally, we start displaying the camera preview.
                mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                        mCaptureCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(
                @NonNull CameraCaptureSession cameraCaptureSession) {
            Log.d(TAG, "Failed");
        }
    };


    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {

        }
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        /**
         * 当有一张图片可用时会回调此方法，但有一点一定要注意： 一定要调用
         * reader.acquireNextImage()和close()方法，否则画面就会卡住！！！！！我被这个坑坑了好久！！！
         * 很多人可能写Demo就在这里打一个Log，结果卡住了，或者方法不能一直被回调。
         **/
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireNextImage();
            /**
             * 因为Camera2并没有Camera1的Priview回调！！！所以该怎么能到预览图像的byte[]呢？就是在这里了！！！
             * 我找了好久的办法！！！
             **/
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            // 这里就是图片的byte数组了
            byte[] bytes = new byte[buffer.remaining()];

            Log.i("huangzheng", "获取到图片啦...   图片大小：" + bytes.length);

            img.close();
        }
    };

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                mImageReader = ImageReader.newInstance(1280, 720,
                        ImageFormat.JPEG, /*maxImages*/2);

                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,mHandler);

                mCameraId = cameraId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            manager.openCamera(mCameraId, mDeviceStateCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new Camera2BasicFragment.ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }



    public static Camera2PreviewTestFragment newInstance() {
        Camera2PreviewTestFragment fragment = new Camera2PreviewTestFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera2_preview_test, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Log.d(TAG, "--mTextureView onViewCreated--");
        mTextureView = (TextureView) view.findViewById(R.id.textureView);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        initLooper();

        if (mTextureView.isAvailable()) {
            Log.d(TAG, "--mTextureView is available--");
        } else {
            Log.d(TAG, "--mTextureView setSurfaceTextureListener--");
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }
}
