package comp5216.sydney.edu.au.vennly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class PlayersActivity extends AppCompatActivity {

    RelativeLayout single;
    RelativeLayout multi;
    VennlyApplication applicationContext;
    GameState gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        single = findViewById(R.id.single_game);
        multi = findViewById(R.id.multi_game);

        applicationContext = (VennlyApplication) this.getApplicationContext();
        gameState = applicationContext.gameState();

        setupListeners();
    }

    private void setupListeners() {
        single.setOnClickListener(view -> {
            applicationContext.setGameMode(Constants.GAMEMODE_SINGLE);
            gameState.addPlayer(Constants.SERVER_ID, applicationContext.iconGenerator().nextIcon());

            Intent intent = new Intent(PlayersActivity.this, GameModeActivity.class);
            startActivity(intent);
        });

        multi.setOnClickListener(view -> {
            Intent intent = new Intent(PlayersActivity.this, HostActivity.class);
            startActivity(intent);
        });
    }
}
