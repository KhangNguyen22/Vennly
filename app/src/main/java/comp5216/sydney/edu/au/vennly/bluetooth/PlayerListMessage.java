package comp5216.sydney.edu.au.vennly.bluetooth;

import java.util.List;

import comp5216.sydney.edu.au.vennly.Player;

public class PlayerListMessage extends BluetoothMessage {
    private List<Player> players;

    public PlayerListMessage(List<Player> players, String sender) {
        super(sender);
        this.players = players;
    }

    public List<Player> getPlayers() {
        return this.players;
    }
}
