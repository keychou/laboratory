package com.mango.bluetoothtest.blemesh.init;

import static com.mango.bluetoothtest.blemesh.common.Constants.DEMO_RUN;
import static com.mango.bluetoothtest.blemesh.common.Constants.SIZE_OF_NETWORK;
import static com.mango.bluetoothtest.blemesh.common.Constants.TEST_TIME_OF_CONVERGENCE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
//import com.instacart.library.truetime.TrueTimeRx;
import com.mango.bluetoothtest.R;
import com.mango.bluetoothtest.ble.BleClientActivity;
import com.mango.bluetoothtest.ble.BleDevAdapter;
import com.mango.bluetoothtest.blemesh.client.BLEClient;
import com.mango.bluetoothtest.blemesh.common.Constants;
import com.mango.bluetoothtest.blemesh.common.RoutingTable;
import com.mango.bluetoothtest.blemesh.common.Utility;
import com.mango.bluetoothtest.blemesh.listeners.Listeners;
import com.mango.bluetoothtest.blemesh.models.Device;
import com.mango.bluetoothtest.blemesh.server.BLEServer;
import com.mango.bluetoothtest.blemesh.tasks.AcceptBLETask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class BleMeshActivity extends Activity {

    private static final String TAG = BleMeshActivity.class.getSimpleName();

    // Per chiedere il GPS su Maps
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static final long HANDLER_PERIOD = 5000;
    private static final int PERMISSION_REQUEST_WRITE = 564;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    public static final int REQUEST_ENABLE_BT = 322;

    private static final String EMAIL_REQUEST = "email";
    private static final String TWITTER_REQUEST = "twitter";

    private TextView debugger, whoAmI, myId;
    private Switch canIBeServerSwitch;
    private DeviceAdapter deviceAdapter;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean canIBeServer = false;

    private boolean isServiceStarted = false;

    private BLEClient client;
    private BLEServer server;

    private AcceptBLETask.OnConnectionRejectedListener connectionRejectedListener;
    private Button startServices, sendTweet, sendEmail;
    //private byte[] lastServerIdFound = new byte[2];


    private long startTime; // Per fare test su tempo convergenza rete;
    private boolean alreadyInizialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long offset = Constants.NO_OFFSET;
        canIBeServer = true;
        alreadyInizialized = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blemesh);
        startServices = findViewById(R.id.startServices);
        debugger = findViewById(R.id.debugger);
        whoAmI = findViewById(R.id.whoAmi);
        myId = findViewById(R.id.myId);
        sendTweet = findViewById(R.id.tweetSomething);
        sendEmail = findViewById(R.id.sendMail);
        canIBeServerSwitch = findViewById(R.id.canIBeServerSwitch);

        sendEmail.setVisibility(View.GONE);
        sendTweet.setVisibility(View.GONE);

        canIBeServerSwitch.setVisibility(View.GONE);
        canIBeServerSwitch.setChecked(canIBeServer);
        canIBeServerSwitch.setOnClickListener(view -> {
            if (canIBeServer) {
                canIBeServer = false;
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "I cannot be a server anymore", Toast.LENGTH_LONG).show());
            } else {
                canIBeServer = true;
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "I can be a server now", Toast.LENGTH_LONG).show());
            }
        });

        askPermissions(savedInstanceState);

        RecyclerView recyclerDeviceList = findViewById(R.id.recy_scan_results);
        deviceAdapter = new DeviceAdapter();
        recyclerDeviceList.setAdapter(deviceAdapter);
        recyclerDeviceList.setVisibility(View.VISIBLE);

        connectionRejectedListener = () -> {
            writeErrorDebug("Connection Rejected, stopping service");
            startServices.performClick();
        };

        /*BleDevAdapter mBleDevAdapter = new BleDevAdapter(new BleDevAdapter.Listener() {
            @Override
            public void onItemClick(BluetoothDevice dev) {
            }
        });*/


        startServices.setOnClickListener(view -> {
            if (isServiceStarted) {
                startServices.setText(R.string.start_service);
                isServiceStarted = false;
                if (server != null) {
                    server.stopServer();
                    server = null;
                } else if (client != null) {
                    client.stopClient();
                    client = null;
                }
                whoAmI.setText(R.string.whoami);
                myId.setText(R.string.myid);
                writeDebug("Service stopped");
                /*if (isScanning) {
                    writeDebug("Stopping Scanning");
                    // Stop the scan, wipe the callback.
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    mScanCallback = null;
                    isScanning = false;
                }
                attemptsUntilServer = 1;
                */
                deviceAdapter.cleanView();
            } else {
                if (TEST_TIME_OF_CONVERGENCE)
                    initializeConvergenceNetworkTimeTest();

                //initializeService();
                startServices.setText(R.string.stop_service);
                isServiceStarted = true;
                cleanDebug();
                writeDebug("Service started");
                if (Utility.isDeviceOnline(this))
                    Log.d(TAG, "OUD: " + "Ho internet");
                if (canIBeServer) {
                    server = BLEServer.getInstance(getApplicationContext());
                    server.setOnDebugMessageListener(new Listeners.OnDebugMessageListener() {
                        @Override
                        public void OnDebugMessage(String message) {
                            writeDebug(message);
                        }

                        @Override
                        public void OnDebugErrorMessage(String message) {
                            writeErrorDebug(message);
                        }
                    });

                    //probably useless
                    //if (lastServerIdFound[0] != (byte) 0) {
                    //    server.setLastServerIdFound(lastServerIdFound);
                    //}
                    server.startServer();
                    server.addServerInitializedListener(() -> new Handler(Looper.getMainLooper()).post(() -> {
                        myId.setText(server.getId());
                        whoAmI.setText(R.string.server);
                        sendEmail.setVisibility(View.VISIBLE);
                        sendTweet.setVisibility(View.VISIBLE);
                    }));
                    server.addOnMessageReceivedWithInternet((idMitt, message) -> {
                        Log.d(TAG, "Message with internet from " + idMitt + " received: " + message);
                        String[] info = message.split(";;");
                        if (info[0].equals(EMAIL_REQUEST)) {
                            try {
                                sendAMail(info[1], info[2], idMitt);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (info[0].equals(TWITTER_REQUEST)) {
                            tweetSomething(info[1]);
                        }
                    });
                    server.setEnoughServerListener((newServer) -> {
                        Log.d(TAG, "OUD: Stop server");
                        server.stopServer();
                        server = null;
                        client = BLEClient.getInstance(getApplicationContext());
                        //if (lastServerIdFound[0] != (byte) 0) {
                        //    client.setLastServerIdFound(lastServerIdFound);
                        //    lastServerIdFound[0] = (byte) 0;
                        //}
                        client.setOnConnectionLostListener(() -> {
                            new Handler(getMainLooper()).post(() -> startServices.performClick());
                            new Handler(getMainLooper()).postDelayed(() -> {
                                Toast.makeText(getApplicationContext(), "Problem with Your server, restart service in 5 seconds", Toast.LENGTH_SHORT).show();
                                startServices.performClick();
                            }, 5000);
                        });
                        client.startClient(newServer);

                        client.addOnClientOnlineListener(() -> {
                            if (client != null) {
                                deviceAdapter.setClient(getApplicationContext());
                                myId.setText(client.getId());
                                whoAmI.setText(R.string.client);
                                sendTweet.setVisibility(View.VISIBLE);
                                sendEmail.setVisibility(View.VISIBLE);
                                client.addReceivedWithInternetListener((idMitt, message) -> {
                                    Log.d(TAG, "Message with internet from " + idMitt + " received: " + message);
                                    String[] info = message.split(";;");
                                    if (info[0].equals(EMAIL_REQUEST)) {
                                        try {
                                            sendAMail(info[1], info[2], idMitt);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (info[0].equals(TWITTER_REQUEST)) {
                                        tweetSomething(info[1]);
                                    }
                                });
                                client.addDisconnectedServerListener((serverId, flags) -> {
                                    new Handler(getMainLooper()).post(() -> startServices.performClick());
                                    new Handler(getMainLooper()).postDelayed(() -> {
                                        Toast.makeText(getApplicationContext(), "Your server is offline, restart service in 5 seconds", Toast.LENGTH_SHORT).show();
                                        startServices.performClick();
                                    }, 5000);
                                });
                            }
                        });

                    });
                    deviceAdapter.setServer(getApplicationContext());
                } else {
                    client = BLEClient.getInstance(getApplicationContext());
                    //if (lastServerIdFound[0] != (byte) 0) {
                    //    client.setLastServerIdFound(lastServerIdFound);
                    //    lastServerIdFound[0] = (byte) 0;
                    //    lastServerIdFound[1] = (byte) 0;
                    //}
                    client.setOnConnectionLostListener(() -> {
                        new Handler(getMainLooper()).post(() -> startServices.performClick());
                        new Handler(getMainLooper()).postDelayed(() -> {
                            Toast.makeText(getApplicationContext(), "Problem with Your server, restart service in 5 seconds", Toast.LENGTH_SHORT).show();
                            startServices.performClick();
                        }, 5000);
                    });
                    client.startClient();

                    client.addOnClientOnlineListener(() -> {
                        deviceAdapter.setClient(getApplicationContext());
                        if (client != null) {
                            myId.setText(client.getId());
                            whoAmI.setText(R.string.client);
                            sendTweet.setVisibility(View.VISIBLE);
                            sendEmail.setVisibility(View.VISIBLE);
                            client.addReceivedWithInternetListener((idMitt, message) -> {
                                Log.d(TAG, "Message with internet from " + idMitt + " received: " + message);
                                String[] info = message.split(";;");
                                if (info[0].equals(EMAIL_REQUEST)) {
                                    try {
                                        sendAMail(info[1], info[2], idMitt);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (info[0].equals(TWITTER_REQUEST)) {
                                    tweetSomething(info[1]);
                                }
                            });
                            client.addDisconnectedServerListener((serverId, flags) -> {
                                new Handler(getMainLooper()).post(() -> startServices.performClick());
                                new Handler(getMainLooper()).postDelayed(() -> {
                                    Toast.makeText(getApplicationContext(), "Your server is offline, restart service in 5 seconds", Toast.LENGTH_SHORT).show();
                                    startServices.performClick();
                                }, 5000);
                            });
                        }

                    });
                }
            }
        });

        sendTweet.setOnClickListener(view -> {
            TextView titleView = new TextView(this);
            titleView.setText(R.string.tweet_something);
            titleView.setGravity(Gravity.CENTER);
            titleView.setPadding(20, 20, 20, 20);
            titleView.setTextSize(20F);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            titleView.setBackgroundColor(Color.WHITE);
            titleView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));


            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme);
            builder.setCustomTitle(titleView);

            final EditText tweetInput = new EditText(this);
            tweetInput.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            tweetInput.setLayoutParams(lp);
            tweetInput.setHint("Tweet");
            tweetInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
            builder.setView(tweetInput);

            builder.setPositiveButton("OK", (dialog, which) -> {

                if (Utility.isDeviceOnline(getApplicationContext())) {
                    tweetSomething(tweetInput.getText().toString());
                } else {
                    String message = TWITTER_REQUEST + ";;" + tweetInput.getText();
                    if (client != null) {
                        client.sendMessage(message, "00", true, new Listeners.OnMessageSentListener() {
                            @Override
                            public void OnMessageSent(String message) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "The message will be delivered by the network", Toast.LENGTH_LONG).show());
                            }

                            @Override
                            public void OnCommunicationError(String error) {
                                //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Errore comunicazione rete: " + error, Toast.LENGTH_LONG).show());
                            }
                        });
                    } else {
                        server.sendMessage(message, "00", true, new Listeners.OnMessageSentListener() {
                            @Override
                            public void OnMessageSent(String message) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "The message will be delivered by the network", Toast.LENGTH_LONG).show());
                            }

                            @Override
                            public void OnCommunicationError(String error) {
                                //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Errore comunicazione rete: " + error, Toast.LENGTH_LONG).show());
                            }
                        });
                    }
                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.create().show();

        });

        sendEmail.setOnClickListener(view -> {
            if ((client == null || client.getConnectBLETask() == null) && (server == null || server.getAcceptBLETask() == null)) {
                Toast.makeText(this, "Not connected in the BLE mesh", Toast.LENGTH_LONG).show();
                return;
            }

            TextView titleView = new TextView(this);
            titleView.setText(R.string.compose_email);
            titleView.setGravity(Gravity.CENTER);
            titleView.setPadding(20, 20, 20, 20);
            titleView.setTextSize(20F);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            titleView.setBackgroundColor(Color.WHITE);
            titleView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme);
            builder.setCustomTitle(titleView);

            final EditText destEmail = new EditText(this);
            destEmail.setTextColor(Color.BLACK);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            destEmail.setLayoutParams(lp);
            destEmail.setHint("Email");

            layout.addView(destEmail);

            final EditText bodyEmail = new EditText(this);
            bodyEmail.setTextColor(Color.BLACK);
            bodyEmail.setLayoutParams(lp);
            bodyEmail.setHint("Body");

            layout.addView(bodyEmail);
            builder.setView(layout);
            builder.setPositiveButton("OK", (dialog, which) -> {

                String id;
                if (client == null) id = server.getId();
                else id = client.getId();

                if (Utility.isDeviceOnline(getApplicationContext())) {
                    try {
                        sendAMail(destEmail.getText().toString(), bodyEmail.getText().toString(), id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = EMAIL_REQUEST + ";;" + destEmail.getText() + ";;" + bodyEmail.getText();
                    if (client != null) {
                        client.sendMessage(message, "00", true, new Listeners.OnMessageSentListener() {
                            @Override
                            public void OnMessageSent(String message) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "The message will be delivered by the network", Toast.LENGTH_LONG).show());
                            }

                            @Override
                            public void OnCommunicationError(String error) {
                                //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Errore comunicazione rete: " + error, Toast.LENGTH_LONG).show());
                            }
                        });
                    } else {
                        server.sendMessage(message, "00", true, new Listeners.OnMessageSentListener() {
                            @Override
                            public void OnMessageSent(String message) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "The message will be delivered by the network", Toast.LENGTH_LONG).show());
                            }

                            @Override
                            public void OnCommunicationError(String error) {
                                //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Errore comunicazione rete: " + error, Toast.LENGTH_LONG).show());
                            }
                        });
                    }
                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.create().show();
        });

    }

    /**
     * Subscribe the activity to routing table updates and when the number of devices reaches Costants.SIZE_OF_NETWORK takes the time
     */
    private void initializeConvergenceNetworkTimeTest() {
        if (alreadyInizialized) return;
        else alreadyInizialized = true;

        startTime = System.nanoTime();

        RoutingTable.getInstance().subscribeToUpdates(new RoutingTable.OnRoutingTableUpdateListener() {
            @Override
            public void OnDeviceAdded(Device device) {
                if (SIZE_OF_NETWORK == RoutingTable.getInstance().getDeviceList().size()) {
                    long endTime = System.nanoTime();
                    long convergenceTime = (endTime - startTime) / 1000000;
                    cleanDebug();
                    writeDebug("Network convergence reached! Number of devices: " + SIZE_OF_NETWORK + ", Time (millis): " + convergenceTime);
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(BleMeshActivity.this, "Network convergence reached! Number of devices: " + SIZE_OF_NETWORK + ", Time (millis): " + convergenceTime, Toast.LENGTH_SHORT).show());
                }

            }

            @Override
            public void OnDeviceRemoved(Device device) {

            }
        });
    }


    /**
     * Clean the field debugger
     */
    private void cleanDebug() {
        runOnUiThread(() -> debugger.setText(""));
    }

    /**
     * Write a message debug into log and text debugger. The message will be logged into the debug logger.
     *
     * @param message message to be written
     */
    private void writeDebug(final String message) {
        runOnUiThread(() -> {
            if (debugger.getLineCount() == debugger.getMaxLines())
                debugger.setText(String.format("%s\n", message));
            else
                debugger.setText(String.format("%s%s\n", String.valueOf(debugger.getText()), message));
        });
        Log.d(TAG, "OUD: " + message);
    }

    /**
     * Write a message debug into log and text debugger. The message will be logged into the error logger.
     *
     * @param message message to be written
     */
    private void writeErrorDebug(final String message) {
        runOnUiThread(() -> {
            if (debugger.getLineCount() == debugger.getMaxLines())
                debugger.setText(String.format("%s\n", message));
            else
                debugger.setText(String.format("%s%s\n", String.valueOf(debugger.getText()), message));
        });
        Log.e(TAG, message);
    }


    private void askPermissions(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkBluetoothAvailability(savedInstanceState);
                if (!DEMO_RUN)
                    askPermissionsExternalStorage();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
            // fix per API < 23
        } else if (PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            checkBluetoothAvailability(savedInstanceState);
        } else {
            // permission not granted, we must decide what to do
            Toast.makeText(this, "Permissions not granted API < 23", Toast.LENGTH_LONG).show();
        }


    }

    /**
     * no need to use external storage to take data, gonna be deprecated
     */
    private void askPermissionsExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                writeDebug("Write storage permissions granted");
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE);
            }
            // fix per API < 23
        } else if (PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            writeDebug("Write storage permissions granted");
        } else {
            // permission not granted, we must decide what to do
            Toast.makeText(this, "Permissions not granted API < 23", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Cattura la risposta asincrona di richiesta dei permessi e se è tutto ok passa a controllare il bluetooth
     *
     * @param requestCode  codice richiesta ( per coarse location = PERMISSION_REQUEST_COARSE_LOCATION )
     * @param permissions  permessi richiesti. NB If request is cancelled, the result arrays are empty.
     * @param grantResults int [] rappresentati gli esiti delle richieste
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: OK");
                    checkBluetoothAvailability();
                    if (!DEMO_RUN)
                        askPermissionsExternalStorage();
                } else {
                    Log.e(TAG, "onRequestPermissionsResult: Permission denied");
                }
                break;
            case PERMISSION_REQUEST_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: OK");
                    writeDebug("Write storage permissions granted");
                    checkBluetoothAvailability();
                } else {
                    writeDebug("Write storage permissions denied");
                }
        }
    }

    /**
     * Controlla che il cellulare supporti l'app e il multiple advertisement. Maschera per onActivityResult e onRequestPermissionsResult
     */
    private void checkBluetoothAvailability() {
        checkBluetoothAvailability(null);
    }

    /**
     * Controlla che il cellulare supporti l'app e il multiple advertisement.
     *
     * @param savedInstanceState se l'app era già attiva non devo reinizializzare tutto
     */
    private void checkBluetoothAvailability(Bundle savedInstanceState) {
        if (canIBeServer) return;
        if (savedInstanceState == null) {
            BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager != null)
                mBluetoothAdapter = mBluetoothManager.getAdapter();

            // Is Bluetooth supported on this device?
            if (mBluetoothAdapter != null) {

                // Is Bluetooth turned on?
                if (mBluetoothAdapter.isEnabled()) {
                    // Are Bluetooth Advertisements supported on this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                        writeDebug("Everything is supported and enabled");
                        if (!DEMO_RUN) {
                            canIBeServer = true;
                            canIBeServerSwitch.setVisibility(View.VISIBLE);
                            canIBeServerSwitch.setChecked(true);
                        }
                    } else {
                        writeDebug("Your device does not support multiple advertisement, you can be only client");
                    }
                } else {
                    // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                setGPSOn();
            } else {
                // Bluetooth is not supported.
                writeDebug(getString(R.string.bt_not_supported));
            }
        }
    }


    /**
     * Makes request to enable GPS
     */
    protected void setGPSOn() {

        /*LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                task1.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    BleMeshActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException | ClassCastException e) {
                            // Ignore the error.
                            writeErrorDebug("GPS: " + e.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        writeErrorDebug("Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.");
                        break;
                }
            }
        });*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        writeDebug("GPS OK");
                        break;
                    case Activity.RESULT_CANCELED:
                        setGPSOn();
                        break;
                }
                break;
            case REQUEST_ENABLE_BT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        checkBluetoothAvailability();
                        break;
                    case Activity.RESULT_CANCELED:
                        writeErrorDebug("Bluetooth is not enabled. Please reboot application.");
                        break;
                }
                break;
        }
    }

    /**
     * Makes a tweet into the feed of the account corresponding the parameters passed to the application
     *
     * @param tweetToUpdate body of the tweet
     */
    private void tweetSomething(String tweetToUpdate) {
        HandlerThread handlerThread = new HandlerThread("Twitter");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(() -> {
            try {
                Twitter twitter = TwitterFactory.getSingleton();
                Status status = twitter.updateStatus(tweetToUpdate);
                Toast.makeText(getApplicationContext(), "Successfully updated the status to \"" + status.getText() + "\".", Toast.LENGTH_LONG).show();
            } catch (TwitterException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Twitter error: " + e.getErrorMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
    }

    /**
     * Send an email
     *
     * @param destEmail destination email address
     * @param body      body field of the email
     * @param idMitt    id of the sender
     * @throws IOException if configuration files were not passed
     */
    private void sendAMail(final String destEmail, String body, final String idMitt) throws IOException {
        /*Properties properties = new Properties();
        InputStream inputStream =
                Objects.requireNonNull(this.getClass().getClassLoader()).getResourceAsStream("email.properties");
        properties.load(inputStream);
        BackgroundMail.newBuilder(this)
                .withUsername(properties.getProperty("email.username"))
                .withPassword(properties.getProperty("email.password"))
                .withMailTo(destEmail)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("A message from BE-Mesh network")
                .withBody(body)
                .withOnSuccessCallback(new BackgroundMail.OnSendingCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Email sent to " + destEmail + " from here by " + idMitt, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR on send email sent to " + destEmail + " from here by " + idMitt, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFail: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .send();*/
    }

    @Override
    protected void onDestroy() {
        if (isServiceStarted) {
            if (client != null) {
                client.stopClient();
                client = null;
            }
            if (server != null) {
                server.stopServer();
                server = null;
            }
            isServiceStarted = false;
        }
        super.onDestroy();
    }
}
