package comp5216.sydney.edu.au.vennly;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GameOverActivity extends AppCompatActivity{
    TextView red, orange, yellow, green, blue, purple, white;
    TextView cat1, cat2, cat3;
    HashMap<String, String> map;

    VennlyApplication applicationContext;
    GameState gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        applicationContext = (VennlyApplication) getApplicationContext();
        gameState = applicationContext.gameState();

        readCategories();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.middle);

        red = (TextView) findViewById(R.id.redResults);
        orange = (TextView) findViewById(R.id.orangeResults);
        yellow = (TextView) findViewById(R.id.yellowResults);
        green = (TextView) findViewById(R.id.greenResults);
        blue = (TextView) findViewById(R.id.blueResults);
        purple = (TextView) findViewById(R.id.purpleResults);
        white = (TextView) findViewById(R.id.whiteResults);

        List<AnsweredPrompt> prompts = new ArrayList<>();

        for (Map<String, AnsweredPrompt> map : gameState.getRoundAnswers()) {
            for (Map.Entry<String, AnsweredPrompt> entry : map.entrySet()) {
                prompts.add(entry.getValue());
            }
        }

        // remove duplicates
        prompts = new ArrayList<>(new HashSet<>(prompts));

        map = new HashMap<>();

        for (AnsweredPrompt prompt: prompts) {
            String currentValue = map.putIfAbsent(prompt.getColour(), prompt.getName());
            if (currentValue != null) {
                currentValue += "\n" + prompt.getName();
                map.put(prompt.getColour(), currentValue);
            }
        }

        LinearLayout diagramLayout = (LinearLayout) findViewById(R.id.diagramPoints);
        PointView pointView = new PointView(this, gameState.getRoundAnswers(), gameState);
        diagramLayout.addView(pointView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView[] views = {red, orange, yellow, green, blue, purple, white};
        String[] colours = {"Red", "Orange", "Yellow", "Green", "Blue", "Purple", "White"};

        for (int i = 0; i < views.length; i++) {
            TextView view = views[i];
            String textToSet = map.get(colours[i]);
            if (textToSet == null) {
                linearLayout.removeView(view);
            } else {
                view.setText(textToSet);
            }

        }

        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(view -> {
            gameState.reset();
            Intent intent = new Intent(GameOverActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void readCategories() {
        Category category = gameState.getCategory();
        String[] categories = category.getCategories();
        cat1 = (TextView) findViewById(R.id.category1);
        cat2 = (TextView) findViewById(R.id.category2);
        cat3 = (TextView) findViewById(R.id.category3);

        cat1.setText(categories[0]);
        cat2.setText(categories[1]);
        cat3.setText(categories[2]);
    }
}

/**
 *  View for rendering points of all player selections
 */
class PointView extends View {
    List<Map<String, AnsweredPrompt>> roundAnswers;
    Map<String, Drawable> iconMap;
    Map<String, String> playerToIcon;
    Map<String, String> uuidToName;
    Context context;

    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    public PointView(Context context, List<Map<String, AnsweredPrompt>> roundAnswers,
                     GameState gameState) {
        super(context);
        this.roundAnswers = roundAnswers;
        this.playerToIcon = gameState.playerToIcon();
        this.uuidToName = gameState.uuidToName();
        this.context = context;

        iconMap = new HashMap<>();
        iconMap.put("monkey", ResourcesCompat.getDrawable(getResources(), R.drawable.monkey, null));
        iconMap.put("cat", ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
        iconMap.put("dog", ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
        iconMap.put("dolphin", ResourcesCompat.getDrawable(getResources(), R.drawable.dolphin, null));
        iconMap.put("rat", ResourcesCompat.getDrawable(getResources(), R.drawable.rat, null));
        iconMap.put("snake", ResourcesCompat.getDrawable(getResources(), R.drawable.snake, null));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Map<String, AnsweredPrompt> answers : roundAnswers) {
            for (Map.Entry<String, AnsweredPrompt> entry : answers.entrySet()) {
                String player = entry.getKey();
                AnsweredPrompt answer = entry.getValue();

                double actualX = answer.getX() * screenWidth;
                double actualY = answer.getY() * screenHeight;

                int promptRadius = (int) (screenWidth * 0.08);

                double left = actualX - (promptRadius / 2.0);
                double top = actualY - (promptRadius / 2.0);
                double right = actualX + (promptRadius / 2.0);
                double bottom = actualY + (promptRadius / 2.0);

                String name = player;
                if (uuidToName.size() != 0) {
                    name = uuidToName.get(player);
                }

                String icon = playerToIcon.get(name);
                Drawable drawable = iconMap.get(icon);
                Rect rect = new Rect((int)left, (int)top, (int)right, (int)bottom);
                if (drawable != null) {
                    drawable.setBounds(rect);
                    drawable.draw(canvas);
                }
            }
        }
    }
}