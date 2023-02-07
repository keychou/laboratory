package com.mango.bluetoothtest.blemesh.tasks;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.mango.bluetoothtest.blemesh.common.Constants;
import com.mango.bluetoothtest.blemesh.common.RoutingTable;
import com.mango.bluetoothtest.blemesh.common.Utility;
import com.mango.bluetoothtest.blemesh.listeners.Listeners;
import com.mango.bluetoothtest.blemesh.models.Server;

import static com.mango.bluetoothtest.blemesh.common.ByteUtility.getBit;
import static com.mango.bluetoothtest.blemesh.common.ByteUtility.printByte;


/**
 * lower tier del BLEClient, implementa le varie primitive di invio e ricezione messaggi, l'inizializzazione del client e la gestione degli errori di base
 */
public class ConnectBLETask {
    private final static String TAG = ConnectBLETask.class.getName();

    private Server server;
    private BluetoothGattCallback mGattCallback;
    private BluetoothGatt mGatt;
    private Context context;
    private String id;
    private String serverId;
    private HashMap<String, String> messageMap;
    private ArrayList<Listeners.OnMessageReceivedListener> receivedListeners;
    private ArrayList<Listeners.OnMessageWithInternetListener> internetListeners;
    private RoutingTable routingTable;
    private Listeners.OnPacketSentListener onPacketSent;
    private Listeners.OnDisconnectedServerListener onDisconnectedServerListener;
    private byte[] lastServerIdFound = new byte[2];
    private boolean temporaryClient;
    private Listeners.OnJobDoneListener onJobDoneListener;
    private boolean jobDone = false;
    private Listeners.OnConnectionLost OnConnectionLostListener;
    private int maxAttempt;

    public ConnectBLETask(Server server, final Context context) {
        // GATT OBJECT TO CONNECT TO A GATT SERVER
        maxAttempt = 0;
        this.context = context;
        this.server = server;
        this.id = null;
        this.messageMap = new HashMap<>();
        receivedListeners = new ArrayList<>();
        internetListeners = new ArrayList<>();
        routingTable = RoutingTable.getInstance();
        temporaryClient = false;
        serverId = "";
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "OUD: Connected to GATT client. Attempting to start service discovery from " + gatt.getDevice().getName());
                    boolean res = gatt.discoverServices();
                    Log.d(TAG, "OUD: onConnectionStateChange: discover services :" + res);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "OUD: " + "onConnectionStateChange: disco  ");
                    if (onDisconnectedServerListener != null)
                        onDisconnectedServerListener.OnDisconnectedServer(serverId, Constants.FLAG_SUSPECTED_DEAD);
                    else {
                        serverId = "";
                        Log.d(TAG, "OUD: id :  " + getId() + (hasCorrectId()));
                        if (hasCorrectId()) OnConnectionLostListener.OnConnectionLost();
                    }
                    /*boolean res = gatt.readDescriptor(gatt.getService(Constants.ServiceUUID).getCharacteristic(Constants.CharacteristicUUID).getDescriptor(Constants.DescriptorUUID));
                    Log.d(TAG, "OUD: " + "onConnectionStateChange: res del read descriptor " + res);*/
                }
                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.d(TAG, "OUD: " + "GATT: " + gatt.toString());
                Log.d(TAG, "OUD: " + "I discovered a service");
                for (BluetoothGattService s : gatt.getServices()) {
                    Log.d(TAG, "OUD: onServicesDiscovered: service uuid: " + s.getUuid());
                }

                BluetoothGattService service = gatt.getService(Constants.ServiceUUID);
                if (service == null) {
                    return;
                }
                Log.d(TAG, "OUD: service : " + service.getUuid().toString());
                for (BluetoothGattCharacteristic s : service.getCharacteristics()) {
                    Log.d(TAG, "OUD: onServicesDiscovered: characteristic uuid: " + s.getUuid());
                }
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constants.CharacteristicUUID);
                if (characteristic == null) {
                    return;
                }
                printByte(lastServerIdFound[0]);
                if (lastServerIdFound[0] != (byte) 0) {
                    BluetoothGattDescriptor desc = characteristic.getDescriptor(Constants.DescriptorCheckAliveUUID);
                    desc.setValue(lastServerIdFound);
                    boolean res = gatt.writeDescriptor(desc);
                    Log.d(TAG, "OUD: " + "Writing descriptor SERVER DEAD? " + desc.getUuid() + " ---> " + res);
                    printByte(lastServerIdFound[0]);
                } else {
                    BluetoothGattDescriptor desc = characteristic.getDescriptor(Constants.DescriptorUUID);
                    boolean res = gatt.readDescriptor(desc);
                    Log.d(TAG, "OUD: " + "descrittore id letto ? " + res);
                }
                // TODO: 05/11/19 che fa sta roba? 
                /*
                for (BluetoothGattService service : gatt.getServices()) {
                    if (service.getUuid().equals(Constants.ServiceUUID)) {
                        if (service.getCharacteristics() != null) {
                            for (BluetoothGattCharacteristic chars : service.getCharacteristics()) {
                                if (chars.getUuid().equals(Constants.CharacteristicUUID)) {
                                    Utility.printByte(lastServerIdFound[0]);
                                    if (lastServerIdFound[0] != (byte) 0) {
                                        BluetoothGattDescriptor desc = gatt.getService(Constants.ServiceUUID).getCharacteristic(Constants.CharacteristicUUID).getDescriptor(Constants.DescriptorCheckAliveUUID);
                                        desc.setValue(lastServerIdFound);
                                        boolean res = gatt.writeDescriptor(desc);
                                        Log.d(TAG, "OUD: " + "Writing descriptor SERVER DEAD? " + desc.getUuid() + " ---> " + res);
                                        Utility.printByte(lastServerIdFound[0]);
                                    }
                                    else {
                                        BluetoothGattDescriptor desc = chars.getDescriptor(Constants.DescriptorUUID);
                                        boolean res = gatt.readDescriptor(desc);
                                        Log.d(TAG, "OUD: " + "descrittore id letto ? " + res);
                                    }

                                }
                            }
                        }
                    }
                }*/
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(TAG, "OUD: " + "I read a characteristic");
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "OUD: " + "I wrote a characteristic");
                    onPacketSent.OnPacketSent(characteristic.getValue());
                } else
                    onPacketSent.OnPacketError("Errore nell'invio del pacchetto, status: " + status);
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                if (characteristic.getUuid().equals(Constants.ClientOnlineCharacteristicUUID)) {
                    routingTable.cleanRoutingTable();
                    byte[] value = characteristic.getValue();
                    for (int i = 1; i < value.length - 1; i++) { //aggiorno l'upper tier
                        boolean flag = false;
                        for (int j = 0; j < 8; j++) {
                            if (getBit(value[i], j) == 1)
                                flag = true;
                        }
                        if (flag) {
                            Log.d(TAG, "OUD: SERVER ONLINE ID: " + i);
                            if (getBit(value[i], 0) == 1) {
                                Log.d(TAG, "OUD: server : " + i);
                                routingTable.addDevice(i, 0);
                            }
                            for (int j = 1; j < 8; j++) {
                                if (getBit(value[i], j) == 1) {
                                    Log.d(TAG, "OUD: client : " + j);
                                    routingTable.addDevice(i, j);
                                }
                            }
                        }
                    }
                    return;
                }

                Log.d(TAG, "OUD: " + "Characteristic changed");
                byte[] value = characteristic.getValue();
                final String valueReceived;
                byte[] correct_message = new byte[value.length - 2];

                byte sorgByte = value[0];
                byte destByte = value[1];
                if (sorgByte == (byte) 255 && destByte == (byte) 255 && value[2] == (byte) 255) {
                    if (onDisconnectedServerListener != null) {
                        Log.d(TAG, "OUD: SERVER DEAD");
                        onDisconnectedServerListener.OnDisconnectedServer(serverId, Constants.FLAG_DEAD);
                        serverId = "";
                    }
                    return;
                }
                final int[] infoDest = Utility.getByteInfo(destByte);

                System.arraycopy(value, 2, correct_message, 0, value.length - 2);

                final String senderId = Utility.getStringId(sorgByte);

                valueReceived = new String(correct_message);
                Log.d(TAG, "OUD: " + valueReceived);

                String previousMsg = messageMap.get(senderId);

                if (previousMsg == null) previousMsg = "";
                messageMap.put(senderId, previousMsg + valueReceived);

                Log.d(TAG, "OUD: " + id + " : Notifica dal server,il mittente " + senderId + " mi ha inviato: " + previousMsg + valueReceived);
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(() -> Toast.makeText(context, "Message received from user " + senderId + " to me ", Toast.LENGTH_LONG).show());
                if (getBit(sorgByte, 0) != 0) {
                    Log.d(TAG, "OUD: " + "NOT last message");
                } else {
                    Log.d(TAG, "OUD: " + "YES last message");

                    if (getBit(destByte, 0) == 1) {
                        //Internet message
                        Log.d(TAG, "OUD: " + "messaggio con internet");
                        for (Listeners.OnMessageWithInternetListener l : internetListeners) {
                            l.OnMessageWithInternet(senderId, messageMap.get(senderId));
                        }
                    } else {
                        Log.d(TAG, "OUD: " + "messaggio normale");
                        String message = messageMap.get(senderId);
                        if (message == null) return;
                        String[] messageSplitted = message.split(";;");
                        int hop = -1;
                        long timestamp = -1;
                        try {
                            hop = Integer.parseInt(messageSplitted[1]);
                            timestamp = Long.parseLong(messageSplitted[0]);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "OUD: " + "Ricevuto messaggio malformato");
                        }
                        for (Listeners.OnMessageReceivedListener l : receivedListeners) {
                            l.OnMessageReceived("" + senderId, messageSplitted[2], hop, timestamp);
                        }
                    }
                    messageMap.remove(senderId);
                }

                super.onCharacteristicChanged(gatt, characteristic);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                if (status == 0) {
                    Log.d(TAG, "OUD: " + "I read a descriptor UUID: " + descriptor.getUuid().toString());
                    if (descriptor.getUuid().toString().equals(Constants.DescriptorUUID.toString())) {
                        setServerId(new String(descriptor.getValue(), Charset.defaultCharset()));
                        Log.d(TAG, "OUD: " + "id : " + getId());
                        boolean res = gatt.readDescriptor(gatt.getService(Constants.ServiceUUID).getCharacteristic(Constants.CharacteristicUUID).getDescriptor(Constants.NEXT_ID_UUID));
                        Log.d(TAG, "OUD: " + "read next id descriptor : " + res);
                    } else if (descriptor.getUuid().toString().equals(Constants.NEXT_ID_UUID.toString())) {
                        setId(getServerId() + new String(descriptor.getValue(), Charset.defaultCharset()));
                        Log.d(TAG, "OUD: " + "id : " + getId());
                        BluetoothGattDescriptor configDesc = gatt.getService(Constants.ServiceUUID).getCharacteristic(Constants.CharacteristicUUID).getDescriptor(Constants.Client_Configuration_UUID);
                        configDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        boolean res = gatt.writeDescriptor(configDesc);
                        Log.d(TAG, "OUD: " + "Writing descriptor?" + configDesc.getUuid() + " --->" + res);
                        BluetoothGattCharacteristic chars = gatt.getService(Constants.ServiceUUID).getCharacteristic(Constants.CharacteristicUUID);
                        gatt.setCharacteristicNotification(chars, true);

                    } else {
                        Log.d(TAG, "OUD: " + "Nessun operazione con tale descrittore : " + descriptor.getUuid().toString());
                    }
                } else if (status == 6) Log.d(TAG, "OUD: " + "id gia inizializzato");
                else Log.d(TAG, "OUD: " + "status non conosciuto");
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d(TAG, "OUD: " + "I wrote a descriptor");
                if (descriptor.getUuid().equals(Constants.Client_Configuration_UUID)) {
                    Log.d(TAG, "OUD: onDescriptorWrite: cc");
                    BluetoothGattService service = gatt.getService(Constants.ServiceUUID);
                    if (service == null) {
                        Log.d(TAG, "OUD: null");
                        return;
                    }
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constants.ClientOnlineCharacteristicUUID);
                    if (characteristic == null) {
                        Log.d(TAG, "OUD: null");
                        return;
                    }
                    BluetoothGattDescriptor desc = characteristic.getDescriptor(Constants.ClientOnline_Configuration_UUID);
                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    boolean res = gatt.writeDescriptor(desc);
                    Log.d(TAG, "OUD: " + "Writing descriptor?" + desc.getUuid() + " --->" + res);
                    gatt.setCharacteristicNotification(characteristic, true);
                } else if (descriptor.getUuid().equals(Constants.ClientOnline_Configuration_UUID)) {
                    if (Utility.isDeviceOnline(context)) {
                        BluetoothGattService service = gatt.getService(Constants.ServiceUUID);
                        if (service == null) {
                            Log.d(TAG, "OUD: service null");
                            return;
                        }
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constants.CharacteristicUUID);
                        if (characteristic == null) {
                            Log.d(TAG, "OUD: characteristic null");
                            return;
                        }
                        BluetoothGattDescriptor desc = characteristic.getDescriptor(Constants.DescriptorClientWithInternetUUID);
                        desc.setValue(getId().getBytes());
                        boolean res = gatt.writeDescriptor(desc);
                        Log.d(TAG, "OUD: " + "Writing descriptor? " + desc.getUuid() + " ---> " + res);
                    }


                } else if (descriptor.getUuid().equals(Constants.DescriptorCheckAliveUUID)) {
                    lastServerIdFound[0] = 0b00000000;
                    lastServerIdFound[1] = 0b00000000;
                    BluetoothGattDescriptor desc = descriptor.getCharacteristic().getDescriptor(Constants.DescriptorUUID);
                    boolean res = gatt.readDescriptor(desc);
                    Log.d(TAG, "OUD: " + "descrittore id letto ? " + res);
                }
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                Log.d(TAG, "OUD: " + "I reliably wrote ");
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.d(TAG, "OUD: " + "I read the remote rssi");
                super.onReadRemoteRssi(gatt, rssi, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }
        };
    }


    /**
     * Send message a un client nella rete
     *
     * @param message  Messaggio da inviare
     * @param dest     Id del Client Destinatario in formato stringa o se ti è piu comodo un altro formato si può cambiare
     * @param listener listener con callback specifica quando il messaggio è stato inviato
     */

    public void sendMessage(byte[] message, String dest, boolean internet, Listeners.OnMessageSentListener listener) {
        Log.d(TAG, "OUD: Send Message : " + new String(message));
        int[] infoSorg = Utility.getIdArrayByString(getId());
        int[] infoDest = Utility.getIdArrayByString(dest);
        byte[][] finalMessage = Utility.messageBuilder(Utility.byteMessageBuilder(infoSorg[0], infoSorg[1]), Utility.byteMessageBuilder(infoDest[0], infoDest[1]), message, internet);

        boolean[] resultHolder = new boolean[1];
        //resultHolder[0] = false;
        int[] indexHolder = new int[1];

        if (finalMessage.length == 1) {
            Log.d(TAG, "OUD: lenght == 1");
            Utility.sendPacket(finalMessage[0], mGatt, new Listeners.OnPacketSentListener() {
                @Override
                public void OnPacketSent(byte[] packet) {
                    if (listener != null) listener.OnMessageSent(new String(message));
                }

                @Override
                public void OnPacketError(String error) {
                    if (listener != null)
                        listener.OnCommunicationError("Error sending packet " + indexHolder[0]);
                }
            });
        } else {
            this.onPacketSent = new Listeners.OnPacketSentListener() {
                @Override
                public void OnPacketSent(byte[] packet) {
                    Log.d(TAG, "OUD: resultHolder: " + resultHolder[0] + ", indexHolder: " + indexHolder[0]);
                    if (indexHolder[0] >= finalMessage.length || !resultHolder[0]) {
                        if (resultHolder[0]) {
                            if (listener != null) listener.OnMessageSent(new String(message));
                            onPacketSent = null;
                        } else {
                            if (listener != null)
                                listener.OnCommunicationError("Error sending packet " + indexHolder[0]);
                        }
                    } else {
                        Log.d(TAG, "OUD: nPacketSent: " + new String(finalMessage[indexHolder[0]]));
                        resultHolder[0] = Utility.sendPacket(finalMessage[indexHolder[0]], mGatt, null);
                        indexHolder[0] += 1;
                    }
                }

                @Override
                public void OnPacketError(String error) {
                    listener.OnCommunicationError(error);
                }
            };

            resultHolder[0] = Utility.sendPacket(finalMessage[indexHolder[0]], this.mGatt, onPacketSent);
            indexHolder[0] += 1;
        }
    }

    public void sendMessage(String message, String dest, boolean internet, Listeners.OnMessageSentListener listener) {
        sendMessage(message.getBytes(), dest, internet, listener);
    }

    public void startClient() {
        this.mGatt = server.getBluetoothDevice().connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        server.setBluetoothGatt(this.mGatt);
        server.getBluetoothGatt().requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        server.getBluetoothGatt().connect();
        setId("");
        Log.d(TAG, "OUD: " + "startClient: " + mGatt.getDevice().getName());
        //boolean ret = this.mGatt.discoverServices();
        //Log.d(TAG, "OUD: " + "DiscoverServices -> " + ret);
    }

    public void stopClient() {
        if ((temporaryClient && mGatt != null) || serverId.equals("") || !hasCorrectId()) {
            Log.d(TAG, "OUD: Sono un client temporaneo sto morendo");
            printByte(lastServerIdFound[0]);
            printByte(lastServerIdFound[1]);
            if (mGatt != null) {
                mGatt.close();
                mGatt = null;
            }
            return;
        }
        byte[] msg = new byte[3];
        msg[0] = (byte) 255;
        msg[1] = (byte) 255;
        msg[2] = (byte) 255;
        if (this.mGatt != null) {
            Log.d(TAG, "OUD: STO QUITTANDO");
            sendMessage(msg, serverId, false, new Listeners.OnMessageSentListener() {
                @Override
                public void OnMessageSent(String message) {
                    Log.d(TAG, "OUD: messaggio quit ok");
                    mGatt.close();
                    mGatt = null;
                }

                @Override
                public void OnCommunicationError(String error) {
                    Log.d(TAG, "OUD: OnCommunicationError: messaggio quit andato male");
                    mGatt.close();
                    mGatt = null;
                }
            });
        }

    }

    public boolean hasCorrectId() {
        return this.id != null && this.id.length() >= 2;
    }

    public String getId() {
        return this.id;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String id) {
        this.serverId = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Listeners.OnMessageReceivedListener> getReceivedListeners() {
        return receivedListeners;
    }

    public void addReceivedListener(Listeners.OnMessageReceivedListener onMessageReceivedListener) {
        this.receivedListeners.add(onMessageReceivedListener);
    }

    public void removeReceivedListener(Listeners.OnMessageReceivedListener onMessageReceivedListener) {
        this.receivedListeners.remove(onMessageReceivedListener);
    }

    public void addReceivedWithInternetListener(Listeners.OnMessageWithInternetListener l) {
        this.internetListeners.add(l);
    }

    public void removeReceivedWithInternetListener(Listeners.OnMessageWithInternetListener l) {
        this.internetListeners.remove(l);
    }

    public void addDisconnectedServerListener(Listeners.OnDisconnectedServerListener l) {
        this.onDisconnectedServerListener = l;
    }

    public void setLastServerIdFound(byte[] s) {
        lastServerIdFound = s;
    }

    public void setCallback(BluetoothGattCallback callback) {
        this.temporaryClient = true;
        this.mGattCallback = callback;
    }

    public void setJobDone() {
        jobDone = true;
        stopClient();
        if (this.onJobDoneListener != null) onJobDoneListener.OnJobDone();
    }

    public boolean getJobDone() {
        return jobDone;
    }

    public BluetoothGatt getmGatt() {
        return mGatt;
    }

    public void setOnJobDoneListener(Listeners.OnJobDoneListener l) {
        this.onJobDoneListener = l;
    }

    public void setOnConnectionLostListener(Listeners.OnConnectionLost l) {
        this.OnConnectionLostListener = l;
    }

    public void restartClient() {
        Log.d(TAG, "OUD: restartClient");
        stopClient();
        maxAttempt++;
        if (maxAttempt == Constants.MAX_ATTEMPTS_RETRY) {
            Log.d(TAG, "OUD: restartClient: mi arrendo, stop definitivo");
            return;
        }
        startClient();
    }

    public int getMaxAttempt() {
        return maxAttempt;
    }
}

