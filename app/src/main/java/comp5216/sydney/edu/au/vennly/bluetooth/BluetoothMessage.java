package comp5216.sydney.edu.au.vennly.bluetooth;

import java.io.Serializable;

public class BluetoothMessage implements Serializable {
    private String sender;
    private String message;

    // Instantiate using this constructor for client messages. Sender will be set prior
    // to sending messages
    public BluetoothMessage() {
    }

    // Use this constructor when don't need to send an actual message and only need to send
    // a message code
    public BluetoothMessage(String sender) {
        this.sender = sender;
    }

    // Use this constructor when wanting to send a full message string.
    public BluetoothMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
