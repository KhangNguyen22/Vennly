package comp5216.sydney.edu.au.vennly;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class GameModeActivity extends AppCompatActivity {
    RelativeLayout venn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);
        venn = findViewById(R.id.game_mode_venn);
        setupListeners();
    }

    private void setupListeners() {
        venn.setOnClickListener(view -> {
            Intent i = new Intent(GameModeActivity.this,CategoriesSavedActivity.class);
            startActivity(i);
        });
    }


}
