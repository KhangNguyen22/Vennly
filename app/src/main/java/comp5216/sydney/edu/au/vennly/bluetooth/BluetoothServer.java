package comp5216.sydney.edu.au.vennly.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class BluetoothServer {
    private BluetoothAdapter bluetoothAdapter;
    private String deviceName;
    private Consumer<Pair<Integer, Object>> onRead;
    private List<ServerThread> serverThreads;
    private final Map<String, ConnectedThread> connectedThreads;
    private final List<UUID> uuids;

    public BluetoothServer(BluetoothAdapter bluetoothAdapter, String deviceName,
                           Consumer<Pair<Integer, Object>> onRead) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.deviceName = deviceName;
        this.onRead = onRead;
        this.serverThreads = new ArrayList<>();
        this.connectedThreads = new HashMap<>();
        this.uuids = new ArrayList<>();
        addUUIDs();
    }

    private void addUUIDs() {
        uuids.add(UUID.fromString("c46986d9-5a41-407d-aad3-00d0f11260da"));
        uuids.add(UUID.fromString("0c1e6e84-c6cd-435a-a244-d723faac142c"));
        uuids.add(UUID.fromString("35c4fc02-d5f4-476d-9313-9c6d4f7320b6"));
        uuids.add(UUID.fromString("3cf57a2a-8c29-43f6-91ba-58742234775f"));
        uuids.add(UUID.fromString("d38d3107-5847-47b0-85b5-c18179be94b7"));
    }

    private void onConnect(String uuid, BluetoothSocket socket) {
        ConnectedThread connectedThread = new ConnectedThread(socket, onRead);
        connectedThread.start();
        synchronized (connectedThreads) {
            connectedThreads.put(uuid, connectedThread);
        }
    }

    public void startServerThreads() {
        for (UUID uuid : uuids) {
            ServerThread serverThread = new ServerThread(bluetoothAdapter, uuid, this::onConnect);
            serverThread.start();
            serverThreads.add(serverThread);
        }
    }

    public void stopServerThreads() {
        for (ServerThread thread : serverThreads) {
            thread.interrupt();
        }

        this.serverThreads = new ArrayList<>();
    }

    /**
     * Sends message to all connected devices
     * @param code
     * @param message
     */
    public void writeMessageToAll(int code, BluetoothMessage message) {
        for (ConnectedThread thread : connectedThreads.values()) {
            thread.writeMessage(code, message);
        }
    }

    /**
     * Sends a message to device with id matching target
     * @param code
     * @param message
     * @param target
     */
    public void writeMessageToTarget(int code, BluetoothMessage message, String target) {
        if (!connectedThreads.containsKey(target)) {
            throw new RuntimeException("Invalid target!");
        }
        ConnectedThread thread = connectedThreads.get(target);
        thread.writeMessage(code, message);
    }

    /**
     * Sends a message to all devices excluding device with id matching target
     * @param code
     * @param message
     * @param target
     */
    public void writeMessageExcludeTarget(int code, BluetoothMessage message, String target) {
        if (!connectedThreads.containsKey(target)) {
            throw new RuntimeException("Invalid target!");
        }

        for (Map.Entry<String, ConnectedThread> entry : connectedThreads.entrySet()) {
            String key = entry.getKey();
            if (key.equals(target)) {
                continue;
            }
            ConnectedThread thread = entry.getValue();
            thread.writeMessage(code, message);
        }

    }
}
