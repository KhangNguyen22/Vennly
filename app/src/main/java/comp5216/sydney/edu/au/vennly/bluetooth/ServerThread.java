package comp5216.sydney.edu.au.vennly.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import comp5216.sydney.edu.au.vennly.Constants;

public class ServerThread extends Thread {
    private UUID uuid;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket clientSocket;
    private OnConnectListener onConnect;

    public ServerThread(BluetoothAdapter bluetoothAdapter, UUID uuid, OnConnectListener onConnect) {
        this.uuid = uuid;
        this.onConnect = onConnect;
        serverSocket = null;

        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.NAME, uuid);
        } catch (IOException e) {
            Log.e("Server", "Error initialising server", e);
        } catch (SecurityException e) {
            Log.e("Server", "Invalid permissions", e);
        }

        if (serverSocket == null) {
            // something went wrong
        }
    }

    public void run() {
        while (true) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                Log.e("Server", "Sockets accept method failed", e);
            }

            onConnect.onConnect(uuid.toString(), clientSocket);
            break;
        }

        cancel();
    }

    private void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e("server", "Could not close the connect socket", e);
        }
    }
}
