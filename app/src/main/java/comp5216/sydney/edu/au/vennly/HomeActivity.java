package comp5216.sydney.edu.au.vennly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class HomeActivity extends AppCompatActivity {

    RelativeLayout newGameCircle;
    RelativeLayout joinGameCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        newGameCircle = findViewById(R.id.new_game_circle);
        joinGameCircle = findViewById(R.id.join_game_circle);

        setupListeners();
    }

    private void setupListeners() {
        newGameCircle.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, PlayersActivity.class);
            startActivity(intent);
        });

        joinGameCircle.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, JoinActivity.class);
            startActivity(intent);
        });
    }
}
