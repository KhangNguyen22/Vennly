package comp5216.sydney.edu.au.vennly.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class ClientThread extends Thread {
    private BluetoothSocket clientSocket;
    private final Consumer<BluetoothSocket> onConnect;
    private Runnable onFail;

    public ClientThread(BluetoothDevice device, UUID uuid, Consumer<BluetoothSocket> onConnect, Runnable onFail) {
        this.onConnect = onConnect;
        this.onFail = onFail;
        clientSocket = null;

        try {
            Log.i("Client UUID", uuid.toString());
            clientSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("Client", "Socket's create() method failed", e);
        } catch (SecurityException e) {
            Log.e("Client", "invalid permissions", e);
        }
    }


    public void run() {
        try {
            // try to connect to server.
            clientSocket.connect();
        } catch (IOException e) {
            Log.e("client", "Error connecting");
            onFail.run();
            return;
        } catch (SecurityException e) {
            Log.e("client", "security exception");
            onFail.run();
            return;
        }

        Log.i("Client", "Connected");

        onConnect.accept(clientSocket);
        //cancel();
    }
}
