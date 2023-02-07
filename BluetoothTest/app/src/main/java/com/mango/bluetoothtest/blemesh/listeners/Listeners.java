package com.mango.bluetoothtest.blemesh.listeners;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

import com.mango.bluetoothtest.blemesh.models.Device;
import com.mango.bluetoothtest.blemesh.models.Server;
import com.mango.bluetoothtest.blemesh.tasks.ConnectBLETask;

public class Listeners {
    public interface OnMessageReceivedListener {
        void OnMessageReceived(String idMitt, String message, int hopNumber, long sendTimeStamp);
    }


    public interface OnMessageWithInternetListener {
        void OnMessageWithInternet(String idMitt, String message);
    }

    public interface OnMessageSentListener {
        void OnMessageSent(String message);

        void OnCommunicationError(String error);
    }

    public interface OnEnoughServerListener {
        void OnEnoughServer(Server server);
    }

    public interface OnDisconnectedServerListener {
        void OnDisconnectedServer(String serverId, byte flags); //flags specificate in Constants.java
    }

    public interface OnScanCompletedListener {
        void OnScanCompleted(ArrayList<Device> devicesFound);
    }

    public interface OnNewServerDiscoveredListener {
        void OnNewServerDiscovered(BluetoothDevice server);

        void OnNewServerNotFound();
    }

    public interface OnDebugMessageListener {
        void OnDebugMessage(String message);

        void OnDebugErrorMessage(String message);
    }

    public interface OnServerInitializedListener {
        void OnServerInitialized();
    }

    public interface OnPacketSentListener {
        void OnPacketSent(byte[] packet);

        void OnPacketError(String error);
    }
    public interface OnJobDoneListener {
        void OnJobDone();
    }

    public interface OnConnectionLost {
        void OnConnectionLost();
    }
}
