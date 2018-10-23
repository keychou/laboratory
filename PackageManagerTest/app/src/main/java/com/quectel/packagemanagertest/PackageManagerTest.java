package com.quectel.packagemanagertest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PackageManagerTest extends AppCompatActivity {

    private final String TAG = "PackageManagerTest";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_manager_test);
        mContext = this;

        if(!isTargetPkgExist("com.quectel.atservice")){
            new AlertDialog.Builder(mContext)
                    .setTitle("Alert")
                    .setMessage("Component absent, press OK to install")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    install(mContext);
                                }
                            }).start();
                        }
                    }).show();
        }
    }


    private void install(Context context){
        try{
            String value = "atservice";
            int key = getResources().getIdentifier(value,"raw",getPackageName());
            Log.d(TAG,"value = " + value + ", key = " + key);
            copyMbnToSD(key, "sdcard/" + value + ".apk");

            Intent intent=new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(new File("sdcard/" + value + ".apk")), type);
            context.startActivity(intent);
        } catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }

    private boolean isTargetPkgExist(String pkgname) {
        boolean isAtServiceInstalled = false;
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            Log.d(TAG, "packageInfo.packageName = " + packageInfo.packageName);
            isAtServiceInstalled = packageInfo.packageName.equals(pkgname);
            if (isAtServiceInstalled){
                break;
            }
        }
        SystemProperties
       // context.sendBroadcastAsUser(intent1, UserHandle.ALL)
        return isAtServiceInstalled;
    }

    private void copyMbnToSD(int originalFileId, String strOutFileName) throws IOException
    {
        InputStream myInput = getResources().openRawResource(originalFileId);;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }


}
