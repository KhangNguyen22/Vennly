package comp5216.sydney.edu.au.vennly;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothMessage;
import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;
import comp5216.sydney.edu.au.vennly.bluetooth.NewConnectionMessage;
import comp5216.sydney.edu.au.vennly.bluetooth.PlayerListMessage;

public class LobbyActivity extends AppCompatActivity {
    private PlayerListAdapter playerListAdapter;
    private BluetoothService bluetoothService;
    private IconGenerator iconGenerator;
    private boolean isHostDevice = false;
    private VennlyApplication applicationContext;
    List<Player> players;
    AppCompatActivity activity = this;
    GameState gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        applicationContext = (VennlyApplication) this.getApplicationContext();
        bluetoothService = applicationContext.bluetoothService();
        gameState = applicationContext.gameState();
        isHostDevice = applicationContext.isHost();
        iconGenerator = applicationContext.iconGenerator();
        String icon;

        players = new ArrayList<>();
        ListView playerListView = (ListView) findViewById(R.id.player_list);
        playerListAdapter = new PlayerListAdapter(this, players, applicationContext.playerName());
        playerListView.setAdapter(playerListAdapter);

        if (isHostDevice) {
            if (!bluetoothService.hasHostPermissions(this)) {
                bluetoothService.getHostPermissions(this);
            } else {
                bluetoothService.setBluetoothName(applicationContext.gameState().lobbyName());
                bluetoothService.makeDeviceDiscoverable(this, () -> {
                    Toast.makeText(this, "Need to make device discoverable to host", Toast.LENGTH_SHORT).show();
                    finish();
                });
                bluetoothService.setHandler(hostHandler);
                bluetoothService.startServer();
                icon = iconGenerator.nextIcon();
                gameState.addPlayer(Constants.SERVER_ID, applicationContext.playerName(), icon);
                players.add(new Player(applicationContext.playerName(), icon, isHostDevice));
            }
        } else {
            Intent intent = getIntent();
            icon = intent.getStringExtra("icon");
            // Newly connected client. Request data of all connected players.
            bluetoothService.writeMessageToServer(Constants.MESSAGE_REQUEST_DATA, new BluetoothMessage());
            bluetoothService.setHandler(clientHandler);
            View button = findViewById(R.id.start_button);
            button.setVisibility(View.GONE);
            players.add(new Player(applicationContext.playerName(), icon, isHostDevice));
        }
    }

    // this method will only be available for the host.
    public void onStartGameClick(View view) {
        Toast.makeText(this, "Pressed start game", Toast.LENGTH_SHORT).show();
        bluetoothService.writeMessageToAll(Constants.MESSAGE_HOST_START, new BluetoothMessage(Constants.SERVER_ID));
        startGame();
    }

    public void startGame() {
        // Host will go choose prompts while other players wait.
        Intent intent;
        if (isHostDevice) {
            bluetoothService.resetBluetoothName();
            intent = new Intent(LobbyActivity.this, GameModeActivity.class);
        } else {
            intent = new Intent(LobbyActivity.this, PromptWaitActivity.class);
        }

        startActivity(intent);
    }

    private final Handler hostHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Log.i("server", "received message");
            BluetoothMessage recvMessage = (BluetoothMessage) message.obj;
            String sender = recvMessage.getSender();

            switch (message.what) {
                case Constants.MESSAGE_NEW_CONNECTION:
                    String name = recvMessage.getMessage();
                    String icon = iconGenerator.nextIcon();
                    Player player = new Player(name, icon);
                    players.add(player);
                    gameState.addPlayer(sender, name, icon);
                    playerListAdapter.notifyDataSetChanged();
                    bluetoothService.writeMessageToTarget(Constants.MESSAGE_RECV_CONNECTION,
                            new BluetoothMessage(Constants.SERVER_ID, icon), sender);
                    bluetoothService.writeMessageExcludeTarget(Constants.MESSAGE_NEW_CONNECTION,
                            new NewConnectionMessage(name, icon, Constants.SERVER_ID), sender);
                    break;

                case Constants.MESSAGE_REQUEST_DATA:
                    bluetoothService.writeMessageToTarget(Constants.MESSAGE_SEND_DATA,
                            new PlayerListMessage(players, Constants.SERVER_ID), sender);
                    break;
            }
        }
    };

    private final Handler clientHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            BluetoothMessage recvMessage = (BluetoothMessage) message.obj;

            switch (message.what) {
                case Constants.MESSAGE_HOST_START:
                    Toast.makeText(activity, "Game started", Toast.LENGTH_SHORT).show();
                    startGame();
                    break;

                case Constants.MESSAGE_NEW_CONNECTION:
                {
                    NewConnectionMessage newConnectionMessage = (NewConnectionMessage) recvMessage;
                    String name = newConnectionMessage.getName();
                    String icon = newConnectionMessage.getIcon();
                    Player player = new Player(name, icon);
                    gameState.addPlayer(name, icon);
                    players.add(player);
                    playerListAdapter.notifyDataSetChanged();
                    break;
                }

                case Constants.MESSAGE_SEND_DATA:
                {
                    PlayerListMessage playerListMessage = (PlayerListMessage) recvMessage;
                    List<Player> hostPlayers = playerListMessage.getPlayers();
                    players.clear();
                    players.addAll(hostPlayers);
                    gameState.clearPlayers();
                    for (Player player : players) {
                        gameState.addPlayer(player.name, player.icon);
                    }
                    playerListAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_BLUETOOTH_PERM_CODE) {
            Log.i("grant", String.valueOf(grantResults.length));
            Log.i("grant", String.valueOf(grantResults[0] == PackageManager.PERMISSION_DENIED));


            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bluetoothService.setBluetoothName(applicationContext.gameState().lobbyName());
                bluetoothService.makeDeviceDiscoverable(this, () -> {
                    Toast.makeText(this, "Need to make device discoverable to host", Toast.LENGTH_SHORT).show();
                    finish();
                });
                bluetoothService.setHandler(hostHandler);
                bluetoothService.startServer();
                String icon = iconGenerator.nextIcon();
                gameState.addPlayer(Constants.SERVER_ID, applicationContext.playerName(), icon);
                players.add(new Player(applicationContext.playerName(), icon, isHostDevice));
            }  else {
                Toast.makeText(this, "Insufficient permissions to host game", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}