package comp5216.sydney.edu.au.vennly;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class HostActivity extends AppCompatActivity {

    Button goButton;
    EditText lobbyNameView;
    EditText playerNameView;
    AlertDialog lobbyAlertDialog;
    AlertDialog playerAlertDialog;
    AlertDialog lobbyPlayerAlertDialog;

    VennlyApplication applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        goButton = findViewById(R.id.go_button);
        lobbyNameView = (EditText) findViewById(R.id.room_name);
        playerNameView  = (EditText) findViewById(R.id.player_name);

        lobbyAlertDialog= new AlertDialog.Builder(HostActivity.this).create();
        lobbyAlertDialog.setMessage("Please enter lobby name");
        lobbyAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());

        playerAlertDialog= new AlertDialog.Builder(HostActivity.this).create();
        playerAlertDialog.setMessage("Please enter player name");
        playerAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());

        lobbyPlayerAlertDialog= new AlertDialog.Builder(HostActivity.this).create();
        lobbyPlayerAlertDialog.setMessage("Please enter lobby and player name");
        lobbyPlayerAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());

        applicationContext = (VennlyApplication) this.getApplicationContext();
        applicationContext.setGameMode(Constants.GAMEMODE_MULTI);
    }


    public void buttonClickHandler(View view){
        String lobbyNameText = lobbyNameView.getText().toString();
        String playerNameText = playerNameView.getText().toString();

        if (!validate(lobbyNameText, playerNameText)) {
            return;
        }

        // Start bluetooth server.
        applicationContext.setPlayerName(playerNameText);
        applicationContext.setIsHost(true);
        applicationContext.gameState().setLobbyName(lobbyNameText);
        Intent intent = new Intent(HostActivity.this, LobbyActivity.class);
        startActivity(intent);
    }

    public boolean validate(String name, String code){
        if (name.length() ==0&& code.length() ==0){
            lobbyPlayerAlertDialog.show();
            return false;
        }
        else if(name.length() == 0 ) {
                lobbyAlertDialog.show();
                return false;
        } else if (code.length() == 0){
                playerAlertDialog.show();
                return false;
        } else{
            return true;
        }
    }
}
