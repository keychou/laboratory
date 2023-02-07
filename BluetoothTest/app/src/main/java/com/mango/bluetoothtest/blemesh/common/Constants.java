package com.mango.bluetoothtest.blemesh.common;

import android.os.ParcelUuid;

import java.util.UUID;

/**
 * Constants for use in the Bluetooth Advertisements sample
 */
public class Constants {

    /**
     * UUID identified with this app - set as Service UUID for BLE Advertisements.
     * <p>
     * Bluetooth requires a certain format for UUIDs associated with Services.
     * The official specification can be found here:
     * {https://www.bluetooth.org/en-us/specification/assigned-numbers/service-discovery}
     */

    public static final UUID NEXT_ID_UUID = UUID.fromString("2122b81d-0000-1000-8000-00805f9b34fb");
    public static final UUID Client_Configuration_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final ParcelUuid Service_UUID = ParcelUuid.fromString("00002019-0000-1000-8000-00805f9b34fb");
    public static final UUID ServiceUUID = UUID.fromString("00002019-0000-1000-8000-00805f9b34fb");

    public static final UUID CharacteristicUUID = UUID.fromString("1111b81d-0000-1000-8000-00805f9b34fb");

    public static final UUID CharacteristicNextServerIdUUID = UUID.fromString("1112b81d-0000-1000-8000-00805f9b34fb");


    public static final UUID DescriptorUUID = UUID.fromString("2222b81d-0000-1000-8000-00805f9b34fb");
    public static final UUID DescriptorClientOnlineUUID = UUID.fromString("2422b81d-0000-1000-8000-00805f9b34fb");
    public static final UUID DescriptorCheckAliveUUID = UUID.fromString("2822b81d-0000-1000-8000-00805f9b34fb");

    public static final UUID DescriptorClientWithInternetUUID = UUID.fromString("2622b81d-0000-1000-8000-00805f9b34fb");
    public static final UUID RoutingTableCharacteristicUUID = UUID.fromString("1211b1d-0000-1000-8000-00805f9b34fb");

    public static final UUID ClientOnlineCharacteristicUUID = UUID.fromString("1212b1d-0000-1000-8000-00805f9b34fb");
    public static final UUID ClientOnline_Configuration_UUID = UUID.fromString("00002903-0000-1000-8000-00805f9b34fb");

    public static final UUID RoutingTableDescriptorUUID = UUID.fromString("2322b81d-0000-1000-8000-00805f9b34fb");


    /**
     * Numero massimo di tentativi di connessione (MAX_ATTEMPTS_UNTIL_SERVER -1)
     */
    public static final int MAX_ATTEMPTS_UNTIL_SERVER = 4;

    /**
     * periodo minimo e max di attesa in secondi
     *
     */
    public static final int SCAN_PERIOD_MIN = 2;
    public static final int SCAN_PERIOD_MAX = 8;
    public static final long NO_OFFSET = Long.MAX_VALUE;

    public static final byte FLAG_SUSPECTED_DEAD = (byte) 0;
    public static final byte FLAG_DEAD = (byte) 1;

    public static final int MAX_ATTEMPTS_RETRY = 5;

    public static final boolean DEMO_RUN = true; // Used to programmatically enable or disable functionalities for the demo
    public static final boolean TEST_TIME_OF_CONVERGENCE = false; // Se true allora il device misura la convergenza della rete
    public static final int SIZE_OF_NETWORK = 3; // questo numero va modificato in base alla grandezza della rete

}