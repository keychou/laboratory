package com.quectel.tf_storage_ext;

import android.content.Context;
import android.nfc.Tag;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

/**
 * Created by klein on 18-9-6.
 */

public class ExternalSdcardManager {
    public final String TAG = "tech-ExtSdcardManager";

    public static final int E_PERMISSION = 1;
    public static final int W_PERMISSION = 2;
    public static final int R_PERMISSION = 4;
    public static final int RW_PERMISSION = 6;
    public static final int RWX_PERMISSION = 7;

    private Context mContext;

    public ExternalSdcardManager(Context context){
        mContext = context;
    }

    //Only be used in android source code
//    public String getSdCardPath(){
//        String path = null;
//        String sdPath = null;
//        String usbPath = null;
//        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
//        List<VolumeInfo> vols = mStorageManager.getVolumes();
//
//        for (int i = 0; i < vols.size(); i++) {
//            DiskInfo disk = vols.get(i).getDisk();
//            path = vols.get(i).path;
//            Log.d(TAG, "path[" + i + "] = " + path);
//            boolean sd = false;
//            boolean usb = false;
//            if (disk != null) {
//                if (disk.isSd()) {
//                    sdPath = path;
//                }else if (disk.isUsb()) {
//                    usbPath = path;
//                }
//            }
//        }
//        Log.d(TAG, "sdPath = " + sdPath + ", usbPath = " + usbPath);
//        return sdPath;
//    }

    /**
     * 通过映射，获取外置内存卡路径
     *
     * @param mContext
     * @param is_removale
     * @return
     */
    public static String getExternalStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getInternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取外置SD卡存储文件的绝对路径
     * Android 4.4以后
     *
     * @param context
     */
    public static String getExternalFileDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();     //此句代码一定要，在内部存储空间创建对应的data目录，但不存储文件
        if (file.exists()) {
            sb.append(getExternalStoragePath(context, true).toString()).append("/Android/data/").append(context.getPackageName())
                    .append("/cache").append(File.separator).toString();
        } else {
            sb.append(getExternalStoragePath(context, true).toString()).append("/Android/data/").append(context.getPackageName())
                    .append("/cache").append(File.separator).toString();
        }
        return sb.toString();
    }

    public DiskStat getDiskCapacity(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlockCount = stat.getBlockCount();
        long feeBlockCount = stat.getAvailableBlocks();
        return new DiskStat(blockSize * feeBlockCount, blockSize
                * totalBlockCount);
    }

    public Vector<String> getFileName(String fileAbsolutePath) {
        Log.d(TAG, "fileAbsolutePath = " + fileAbsolutePath);
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        Log.d(TAG, "checkPerssion(file) " + checkFilePerssion(file));

        File[] subFile = file.listFiles();
        if (subFile != null){
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {

                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    Log.d("eee","文件名 ： " + filename);
                }
            }
        } else {
            Log.d(TAG, "directory is null");
        }
        return vecFile;
    }

    public void deleteFilesProceed(String fileAbsolutePath, long n){
        Log.d(TAG, "fileAbsolutePath = " + fileAbsolutePath);

        int count = 0;
        File file = new File(fileAbsolutePath);
        Log.d(TAG, "checkPerssion(file) " + checkFilePerssion(file));

        File[] subFile = file.listFiles();
        if (subFile != null){
            for (int i = 0; i < subFile.length; i++) {
                if (!subFile[i].isDirectory()) {
                    if (++count > n){
                        break;
                    }
                    subFile[i].delete();
                    Log.d(TAG, "deleted : " + subFile[i].getName());
                }
            }
        } else {
            Log.d(TAG, "directory is null");
        }
    }

    public int checkFilePerssion(File file){
        int nodeperssion = 0;
        if (file.canRead()){
            nodeperssion = nodeperssion + 4;
        }

        if (file.canWrite()){
            nodeperssion = nodeperssion + 2;
        }

        if (file.canExecute()){
            nodeperssion = nodeperssion + 1;
        }

        return nodeperssion;
    }
}
