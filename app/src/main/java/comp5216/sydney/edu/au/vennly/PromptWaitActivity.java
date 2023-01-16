package comp5216.sydney.edu.au.vennly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;
import comp5216.sydney.edu.au.vennly.bluetooth.GameStateMessage;

/**
 *  Non-host players will be on this screen until the host connects.
 */
public class PromptWaitActivity extends AppCompatActivity {
    VennlyApplication applicationContext;
    GameState gameState;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_wait);

        applicationContext = (VennlyApplication) this.getApplicationContext();
        BluetoothService bluetoothService = applicationContext.bluetoothService();
        gameState = applicationContext.gameState();
        bluetoothService.setHandler(handler);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case Constants.MESSAGE_HOST_START:
                    Intent intent = new Intent(PromptWaitActivity.this, GameplayActivity.class);
                    startActivity(intent);
                    break;

                case Constants.MESSAGE_GAME_STATE:
                    GameStateMessage recvMessage = (GameStateMessage) message.obj;
                    GameState hostGameState = recvMessage.getGameState();
                    if (gameState == null) {
                        Log.i("prompt", "Game state is null");
                    } else {
                        Log.i("prompt", "game state is not null");
                    }
                    gameState.setCategory(hostGameState.getCategory());
                    gameState.setPrompt(hostGameState.getPrompt());
                    break;
            }
        }
    };

}