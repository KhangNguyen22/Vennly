package comp5216.sydney.edu.au.vennly.bluetooth;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import comp5216.sydney.edu.au.vennly.GameState;

public class GameStateMessage extends BluetoothMessage {
    private GameState gameState;

    public GameStateMessage(GameState gameState, String sender) {
        super(sender);
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
