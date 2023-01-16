package comp5216.sydney.edu.au.vennly.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public class ConnectedThread extends Thread {
    private BluetoothSocket socket;
    private ObjectInputStream objectInStream;
    private ObjectOutputStream objectOutStream;
    private Consumer<Pair<Integer, Object>> onRead;
    private Runnable onFail;

    public ConnectedThread(BluetoothSocket socket, Consumer<Pair<Integer, Object>> onRead) {
        this.socket = socket;
        this.onRead = onRead;
        InputStream inStream = null;
        OutputStream outStream = null;

        try {
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("Connected", "Error getting streams", e);
        }

        if (inStream == null || outStream == null) {
            // something went wrong
            Log.e("asdasd", "asd");
            return;
        }

        try {
            objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.flush();
        } catch (IOException e) {
            Log.e("connected thread", "Exception during creating objectoutstream", e);
        }

        try {
            objectInStream = new ObjectInputStream(inStream);
        } catch (IOException e) {
            Log.e("connected thread", "Exception during creating objectinstream", e);
        }
    }

    public void run() {
        while (true) {
            Pair<Integer, Object> message = readMessage();
            onRead.accept(message);
        }

    }

    private Pair<Integer, Object> readMessage() {
        int code = -1;
        Object message = null;

        try {
            code = objectInStream.readInt();
        } catch (IOException e) {
            Log.e("Connected thread", "failed to read message code", e);
        }

        try {
            if (code == -1) {
                throw new IOException();
            }
            message = (BluetoothMessage) objectInStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("Connected thread", "failed to read message code", e);
        }

        return new Pair<>(code, message);
    }

    public void writeMessage(Integer code, Object message) {
        try {
            objectOutStream.writeInt(code);
            BluetoothMessage tmpMessage = (BluetoothMessage) message;
            objectOutStream.writeObject(tmpMessage);
            objectOutStream.flush();
        } catch (IOException e) {
            Log.e("connected thread", "Exception during write", e);
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("Connected thread", "close() of connect socket failed", e);
        }
    }
}
