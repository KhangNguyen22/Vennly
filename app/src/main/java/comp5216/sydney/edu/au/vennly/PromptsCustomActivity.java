package comp5216.sydney.edu.au.vennly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PromptsCustomActivity extends AppCompatActivity {

    private Button savedPromptsButton;
    private Button saveButton;
    private EditText categoryTitle;
    private EditText promptsEntry;
    private File promptsFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompts_custom);
        savedPromptsButton = findViewById(R.id.savedButtonPrompts);
        saveButton= findViewById(R.id.saveButton);
        categoryTitle = findViewById(R.id.textViewCollectionName);
        promptsEntry = findViewById(R.id.textViewPromptsEntry);
        promptsFile = new File(getApplicationContext().getFilesDir(), "prompts.json");
        setupListeners();
    }

    private void setupListeners() {
        savedPromptsButton.setOnClickListener(view -> {
            // Go to saved prompts activity
            Intent intent = new Intent(PromptsCustomActivity.this, PromptsSavedActivity.class);
            startActivity(intent);
        });

        saveButton.setOnClickListener(view -> {
            // Handle saving prompts to file
            if (checkTextFields()) {
                String categoryName = categoryTitle.getText().toString();
                String[] prompts = splitPrompts(promptsEntry.getText().toString());

                if (writePromptsToFile(categoryName, prompts)) {
                    Toast.makeText(PromptsCustomActivity.this, "Prompts Saved!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PromptsCustomActivity.this, PromptsSavedActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(PromptsCustomActivity.this, "Prompts Failed to Save!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkTextFields() {
        boolean valid = true;
        if (categoryTitle.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a category name!", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (promptsEntry.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter some prompts!", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    private String[] splitPrompts(String prompts) {
        return prompts.split(",");
    }

    //If there is no existing JSON file then create one
    private void createPromptsFile() throws JSONException, IOException {
        JSONObject promptObject = new JSONObject();
        JSONArray promptsList = new JSONArray();
        promptObject.put("prompts", promptsList);
        FileWriter fileWriter = new FileWriter(promptsFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(promptObject.toString());
        bufferedWriter.close();

    }

    //Reads the existing JSON File
    //Reference: https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
    private String readJSONFile() throws IOException, JSONException {
        if (!promptsFile.exists()) {
            createPromptsFile();
        }
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

    //Writes data entered by user into prompts.json
    private boolean writePromptsToFile(String categoryName, String[] prompts){
        String promptsJSON;
        try {
            promptsJSON = readJSONFile();
            JSONObject obj = new JSONObject(promptsJSON);
            JSONArray promptList = obj.getJSONArray("prompts");
            JSONObject newPrompt = new JSONObject();
            JSONArray newPromptOptions = new JSONArray();
            for (String prompt : prompts) {
                newPromptOptions.put(prompt);
            }
            newPrompt.put("prompt", categoryName);
            newPrompt.put("names", newPromptOptions);
            promptList.put(newPrompt);
            FileWriter fileWriter = new FileWriter(promptsFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(obj.toString());
            bufferedWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
