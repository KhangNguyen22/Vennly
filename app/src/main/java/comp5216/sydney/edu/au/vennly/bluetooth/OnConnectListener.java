package comp5216.sydney.edu.au.vennly.bluetooth;

import android.bluetooth.BluetoothSocket;

/**
 *  Functional Interface to represent callback for when bluetooth server accepts a connection
 */
@FunctionalInterface
public interface OnConnectListener {
    void onConnect(String uuid, BluetoothSocket bluetoothSocket);
}
