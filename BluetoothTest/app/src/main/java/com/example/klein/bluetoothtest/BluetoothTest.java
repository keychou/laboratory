package com.example.klein.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PermissionInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BluetoothTest extends AppCompatActivity {
    private static final String TAG = "BluetoothTest";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);

        BluetoothManager manager = (BluetoothManager) getSystemService(
                Context.BLUETOOTH_SERVICE);
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothAdapter = manager.getAdapter();

        ScanSettings opportunisticScanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC)
                .build();
        BleScanCallback emptyScanCallback = new BleScanCallback();

        // No scans are really started with opportunistic scans only.
        mScanner.startScan(Collections.<ScanFilter>emptyList(), opportunisticScanSettings,
                emptyScanCallback);

        Log.d(TAG, "klein_bluetooth--opportunisticScanSettings = "
                + emptyScanCallback.getScanResults().isEmpty());

        (new Exception()).printStackTrace();
    }



    // Helper class for BLE scan callback.
    private class BleScanCallback extends ScanCallback {
        private Set<ScanResult> mResults = new HashSet<ScanResult>();
        private List<ScanResult> mBatchScanResults = new ArrayList<ScanResult>();

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "--result = " + result.getDevice());

            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                Log.d(TAG, "--onScanResult--result add= " + result.getDevice());
                mResults.add(result);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // In case onBatchScanResults are called due to buffer full, we want to collect all
            // scan results.
            Log.d(TAG, "--onBatchScanResults");
            mBatchScanResults.addAll(results);
        }

        // Clear regular and batch scan results.
        synchronized public void clear() {
            Log.d(TAG, "--clear");

            mResults.clear();
            mBatchScanResults.clear();
        }

        // Return regular BLE scan results accumulated so far.
        synchronized Set<ScanResult> getScanResults() {
            Log.d(TAG, "--getScanResults");

            return Collections.unmodifiableSet(mResults);
        }

        // Return batch scan results.
        synchronized List<ScanResult> getBatchScanResults() {
            Log.d(TAG, "--getBatchScanResults");

            return Collections.unmodifiableList(mBatchScanResults);
        }
    }
}
