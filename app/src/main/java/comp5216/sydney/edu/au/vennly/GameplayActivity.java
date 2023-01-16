package comp5216.sydney.edu.au.vennly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.MessageFormat;
import java.util.List;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothMessage;
import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;
import comp5216.sydney.edu.au.vennly.bluetooth.PromptMessage;
import comp5216.sydney.edu.au.vennly.bluetooth.RoundAnswersMessage;

import java.util.Map;

public class GameplayActivity extends AppCompatActivity {
    //Timer Variables
    CountDownTimer cTimer;
    TextView timerText;
    int secondsLeft = 600;

    //Venn Variables
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    Bitmap vennBitmap;
    ImageView venn;

    //Category Variables
    TextView cat1, cat2, cat3;

    //Prompt Variables
    //File promptFile;
    JSONArray prompts;
    int prompt_i = 0;
    String currentPrompt;
    TextView promptTextBox;

    boolean updateTimer;
    boolean isMultiplayer;
    boolean isHost;
    VennlyApplication applicationContext;
    BluetoothService bluetoothService;
    GameState gameState;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = (VennlyApplication) this.getApplicationContext();
        gameState = applicationContext.gameState();
        updateTimer = true;

        setContentView(R.layout.activity_gameplay);
        timerText = (TextView) findViewById(R.id.timer);
        readCategories();
        getPrompts();
        createVennButton();
        startTimer();
        incrementPrompt();

        isMultiplayer = applicationContext.gameMode() == Constants.GAMEMODE_MULTI;
        if (isMultiplayer) {
            isHost = applicationContext.isHost();
            bluetoothService = applicationContext.bluetoothService();
            if (applicationContext.isHost()) {
                handler = hostHandler;
            } else {
                handler = clientHandler;
            }
            bluetoothService.setHandler(handler);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    protected void onStop() {
        super.onStop();
        if (cTimer != null) {
            cTimer.cancel();
        }
    }

    private void getPrompts() {
        promptTextBox = (TextView) findViewById(R.id.promptBox);
        List<String> promptsArray = gameState.getPrompt().getPrompts();
        prompts = new JSONArray(promptsArray);
        try {
            promptTextBox.setText(prompts.get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void incrementPrompt() {
        if (prompt_i < prompts.length()) {
            try {
                currentPrompt = prompts.get(prompt_i).toString();
                promptTextBox.setText(currentPrompt);
                gameState.startNewRound();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            prompt_i++;
        } else {
            cancelTimer();

            if (isMultiplayer) {
                if (isHost) {
                    Log.i("game state", String.valueOf(gameState.getRoundAnswers().size()));
                    gameState.setRoundAnswers(gameState.getRoundAnswers());
                    bluetoothService.writeMessageToAll(Constants.MESSAGE_GAME_STATE,
                            new RoundAnswersMessage(gameState, Constants.SERVER_ID, true));
                    bluetoothService.writeMessageToAll(Constants.MESSAGE_GAME_FINISHED,
                            new BluetoothMessage(Constants.SERVER_ID));
                    Intent intent = new Intent(GameplayActivity.this, GameOverActivity.class);
                    startActivity(intent);

                }
            } else {
                Intent intent = new Intent(GameplayActivity.this, GameOverActivity.class);
                startActivity(intent);
            }
        }
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

    void startTimer() {
        updateTimer = true;
        cTimer = new CountDownTimer(Constants.GAMEPLAY_TIME_LIMIT, 1000) {
            public void onTick(long ms) {
                if (Math.round((float)ms / 1000.0f) < secondsLeft)
                {
                    secondsLeft = Math.round((float)ms / 1000.0f);
                    if (updateTimer) {
                        timerText.setText(MessageFormat.format("seconds remaining: {0}", secondsLeft));
                    }
                }
                //Log.i("test","ms="+ms+" till finished="+secondsLeft );
            }
            public void onFinish() {
                if (isMultiplayer) {
                    if (isHost) {
                        bluetoothService.writeMessageToAll(Constants.MESSAGE_CONTINUE,
                                new BluetoothMessage(Constants.SERVER_ID));
                        nextPrompt();
                    }
                } else {
                    nextPrompt();
                }
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if(cTimer!=null) {
            updateTimer = false;
            cTimer.cancel();
            //timerText.setText("00");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createVennButton() {
        Drawable vennDrawable = getResources().getDrawable(R.drawable.ic_venndiagram, getTheme());
        venn = (ImageView) findViewById(R.id.vennDiagram);
        vennBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(vennBitmap);
        vennDrawable.setBounds(0,0, canvas.getWidth(), canvas.getHeight());
        vennDrawable.draw(canvas);


        venn.setOnTouchListener((view, motionEvent) -> {
            String colour;
            int eventPadTouch = motionEvent.getAction();
            if (eventPadTouch == MotionEvent.ACTION_DOWN) {//Action Down is the action corresponding to a touch
                //Grab the coordinates of the touch
                double iX = motionEvent.getX();
                double iY = motionEvent.getY();
                //Ensure those coordinates are in the bounds of the image
                if (iX >= 0 & iY >= 0 & iX < vennBitmap.getWidth() & iY < vennBitmap.getHeight()) {
                    @ColorInt int pixelColour = vennBitmap.getPixel((int) iX, (int) iY);
                    colour = String.valueOf(pixelColour);
                    if (pixelColour != 0) {

                        AnsweredPrompt prompt = new AnsweredPrompt(currentPrompt, iX / screenWidth, iY / screenHeight);
                        switch (pixelColour) {
                            case -1:
                                colour = "White";
                                break;
                            case -3310:
                                colour = "Yellow";
                                break;
                            case -16772097:
                                colour = "Blue";
                                break;
                            case -577734:
                                colour = "Red";
                                break;
                            case -13243083:
                                colour = "Green";
                                break;
                            case -227811:
                                colour = "Orange";
                                break;
                            case -9683980:
                                colour = "Purple";
                                break;
                            default:
                                colour = "Do nothing";
                                return true;

                        }
                        prompt.setColour(colour);
                        onSelectCategory(prompt);
                    }
                }
                return true;
            }
            return false;
        });
    }

    public void nextPrompt() {
        incrementPrompt();
        secondsLeft = 600;
        startTimer();
        gameState.startNewRound();
    }

    private void onSelectCategory(AnsweredPrompt prompt) {
        cancelTimer();

        if (isMultiplayer) {
            updateTimer = false;
            timerText.setText(R.string.gameplay_waiting);

            if (isHost) {
                gameState.addPlayerAnswer(Constants.SERVER_ID, prompt);

                if (gameState.numPlayersAnswersCurrentRound() == gameState.getPlayers().size()) {
                    bluetoothService.writeMessageToAll(Constants.MESSAGE_CONTINUE,
                            new BluetoothMessage(Constants.SERVER_ID));
                    nextPrompt();
                }

            } else {
                // Send chosen category to host
                BluetoothMessage bluetoothMessage = new PromptMessage(prompt);
                bluetoothService.writeMessageToServer(Constants.MESSAGE_CATEGORY, bluetoothMessage);
            }
        } else {
            Log.i("Prompt", prompt.getName());
            gameState.addPlayerAnswer(Constants.SERVER_ID, prompt);
            nextPrompt();
        }
    }

    private final Handler hostHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            BluetoothMessage recvMessage = (BluetoothMessage) message.obj;
            String sender = recvMessage.getSender();
            if (message.what == Constants.MESSAGE_CATEGORY) {
                AnsweredPrompt prompt = ((PromptMessage) recvMessage).getPrompt();

                // thread safety so don't end up writing continue message multiple times
                synchronized (gameState) {
                    gameState.addPlayerAnswer(sender, prompt);
                    if (gameState.numPlayersAnswersCurrentRound() == gameState.getPlayers().size()) {
                        bluetoothService.writeMessageToAll(Constants.MESSAGE_CONTINUE,
                                new BluetoothMessage(Constants.SERVER_ID));
                        nextPrompt();
                    }
                }
            }
        }
    };

    private final Handler clientHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            BluetoothMessage recvMessage = (BluetoothMessage) message.obj;

            switch (message.what) {
                case Constants.MESSAGE_CONTINUE:
                    nextPrompt();
                    break;

                case Constants.MESSAGE_GAME_STATE:
                    Log.i("client", "game state received");
                    RoundAnswersMessage roundAnswersMessage = (RoundAnswersMessage) recvMessage;
                    List<Map<String, AnsweredPrompt>> roundAnswers = roundAnswersMessage.getRoundAnswers();
                    Log.i("game state", String.valueOf(roundAnswers.size()));
                    gameState.setRoundAnswers(roundAnswers);
                    break;

                case Constants.MESSAGE_GAME_FINISHED:
                    Intent intent = new Intent(GameplayActivity.this, GameOverActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

}


