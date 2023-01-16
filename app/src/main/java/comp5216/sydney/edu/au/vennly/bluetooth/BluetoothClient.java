package comp5216.sydney.edu.au.vennly.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import comp5216.sydney.edu.au.vennly.Constants;

public class BluetoothClient {
    private final BluetoothDevice bluetoothDevice;
    private ClientThread clientThread;
    private ConnectedThread connectedThread;
    private String clientName;
    private Consumer<Pair<Integer, Object>> onRead;
    private List<UUID> uuids;
    private List<Thread> clientThreads;
    private int currentUUID;
    private String clientID;

    public BluetoothClient(BluetoothDevice bluetoothDevice, String clientName,
                           Consumer<Pair<Integer, Object>> onRead) {
        this.bluetoothDevice = bluetoothDevice;
        this.onRead = onRead;
        this.clientName = clientName;
        this.clientThread = null;
        this.connectedThread = null;
        this.clientThreads = new ArrayList<>();
        this.uuids = new ArrayList<>();
        currentUUID = 0;

        addUUIDs();
    }

    private void addUUIDs() {
        uuids.add(UUID.fromString("c46986d9-5a41-407d-aad3-00d0f11260da"));
        uuids.add(UUID.fromString("0c1e6e84-c6cd-435a-a244-d723faac142c"));
        uuids.add(UUID.fromString("35c4fc02-d5f4-476d-9313-9c6d4f7320b6"));
        uuids.add(UUID.fromString("3cf57a2a-8c29-43f6-91ba-58742234775f"));
        uuids.add(UUID.fromString("d38d3107-5847-47b0-85b5-c18179be94b7"));
    }

    public void startClient() {
        startThread();
    }


    // each uuid corresponds to a thread on the server and can accept at most one client.
    // if we fail to connect to one server thread, it means that another client connected to it.
    // therefore we just iterate through all potential uuids until we get one that succeeds.
    private void startThread() {
        if (currentUUID >= uuids.size()) {
            return;
        }

        if (clientThread != null) {
            clientThread.interrupt();
            clientThread = null;
        }

        UUID uuid = uuids.get(currentUUID);
        clientThread = new ClientThread(bluetoothDevice, uuid, this::onConnect, this::startThread);
        clientThread.start();
        currentUUID++;
    }

    public void stopClientThreads() {
        for (Thread thread : clientThreads) {
            thread.interrupt();
        }
        this.clientThreads = new ArrayList<>();
    }

    private void onConnect(BluetoothSocket socket) {
        clientThread = null;
        connectedThread = new ConnectedThread(socket, onRead);
        connectedThread.start();
        stopClientThreads();
        clientID = uuids.get(currentUUID - 1).toString();
        Log.i("Device name", clientName);

        writeMessage(Constants.MESSAGE_NEW_CONNECTION, new BluetoothMessage(clientID, clientName));
    }


    public void writeMessage(int code, BluetoothMessage message) {
        if (connectedThread == null) {
            Log.i("Client", "attempted to write a message but not connected to server");
            return;
        }

        message.setSender(clientID);
        connectedThread.writeMessage(code, message);
    }
}
