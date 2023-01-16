package comp5216.sydney.edu.au.vennly.bluetooth;

public class NewConnectionMessage extends BluetoothMessage {
    private String name;
    private String icon;

    public NewConnectionMessage(String name, String icon, String sender) {
        super(sender);
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}
