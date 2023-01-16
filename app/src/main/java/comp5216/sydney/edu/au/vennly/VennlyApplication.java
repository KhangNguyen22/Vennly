package comp5216.sydney.edu.au.vennly;


import android.app.Application;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;

/**
 *  VennlyApplication
 *  -----------------
 *  Stores global application context information such as game state and bluetooth connection
 *  status
 */
public class VennlyApplication extends Application {
    private final BluetoothService bluetoothService;
    private final GameState gameState;
    private final IconGenerator iconGenerator;
    private boolean isHost;
    private String playerName;
    private int gameMode;

    public VennlyApplication() {
        bluetoothService = new BluetoothService();
        gameState = new GameState();
        iconGenerator = new IconGenerator();
        isHost = false;
        gameMode = Constants.GAMEMODE_SINGLE;
    }

    public BluetoothService bluetoothService() {
        return bluetoothService;
    }

    public GameState gameState() {
        return gameState;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String playerName() {
        return playerName;
    }

    public void setGameMode(int gameMode) {
        if (gameMode != Constants.GAMEMODE_SINGLE && gameMode != Constants.GAMEMODE_MULTI) {
            return;
        }

        this.gameMode = gameMode;
    }

    public int gameMode() {
        return gameMode;
    }

    public IconGenerator iconGenerator() {
        return iconGenerator;
    }
}
