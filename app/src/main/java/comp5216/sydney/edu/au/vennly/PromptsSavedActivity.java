package comp5216.sydney.edu.au.vennly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothMessage;
import comp5216.sydney.edu.au.vennly.bluetooth.BluetoothService;
import comp5216.sydney.edu.au.vennly.bluetooth.GameStateMessage;

public class PromptsSavedActivity extends AppCompatActivity {

    private File promptsFile;
    private GameState gameState;
    private BluetoothService bluetoothService;
    private boolean isMultiplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompts_save);
        VennlyApplication applicationContext = (VennlyApplication) getApplicationContext();
        gameState = applicationContext.gameState();
        bluetoothService = applicationContext.bluetoothService();
        isMultiplayer = applicationContext.gameMode() == Constants.GAMEMODE_MULTI;

        promptsFile = new File(getApplicationContext().getFilesDir(), "prompts.json");
        List<Prompt> prompts = new ArrayList<>();
        if(promptsFile.exists()) {
            String data = null;
            try {
                data = readJSONFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            populatePrompts(prompts, data);
        }

        ListView promptListView = findViewById(R.id.promptList);
        PromptListAdapter promptListAdapter = new PromptListAdapter(this, prompts, (prompt) -> {
            gameState.setPrompt(prompt);

            if (isMultiplayer) {
                Log.i("Category", gameState.getCategory().getCategoryName());
                bluetoothService.writeMessageToAll(Constants.MESSAGE_GAME_STATE,
                        new GameStateMessage(gameState, Constants.SERVER_ID));
                bluetoothService.writeMessageToAll(Constants.MESSAGE_HOST_START,
                        new BluetoothMessage(Constants.SERVER_ID));
            }
        });
        promptListView.setAdapter(promptListAdapter);

        setupListeners();
    }

    //Clicking on the custom button should switch to the custom activity
    private void setupListeners(){
        Button customButton = findViewById(R.id.customButton);
        customButton.setOnClickListener(view -> {

            //The code here will switch to the custom prompts page
            Intent intent = new Intent(PromptsSavedActivity.this, PromptsCustomActivity.class);
            startActivity(intent);
        });

    }

    //Read the JSON file from local storage
    //Reference: https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    private String readJSONFile() throws IOException{
        FileReader fileReader = new FileReader(promptsFile);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    //Populates the prompts array from the JSON file so that it can displayed
    //Citation Acknowledgement: https://stackoverflow.com/questions/11874919/parsing-json-string-in-java
    private void populatePrompts(List<Prompt> prompts, String data){
        try {
            final JSONObject obj = new JSONObject(data);
            final JSONArray promptsJSON = obj.getJSONArray("prompts");
            final int n = promptsJSON.length();
            for(int i = 0; i < n; i++){
                JSONObject prompt = promptsJSON.getJSONObject(i);
                String promptName = prompt.getString("prompt");
                ArrayList<String> examples = new ArrayList<>();
                JSONArray promptExamples = prompt.getJSONArray("names");
                for(int j = 0; j < promptExamples.length(); j++){
                    examples.add(promptExamples.getString(j));
                }
                prompts.add(new Prompt(promptName, examples));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
