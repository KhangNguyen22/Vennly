package comp5216.sydney.edu.au.vennly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CategoriesCustomActivity extends AppCompatActivity {
    EditText categoryName;
    EditText categoryText1;
    EditText categoryText2;
    EditText categoryText3;
    Button next;
    File categoryFile;
    Button savedCategoriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_custom);
        categoryName = findViewById(R.id.categoryName);
        categoryText1 = findViewById(R.id.textViewCategory1);
        categoryText2 = findViewById(R.id.textViewCategory2);
        categoryText3 = findViewById(R.id.textViewCategory3);
        next = findViewById(R.id.nextButtonCategoriesCustomSelected);
        savedCategoriesButton = findViewById(R.id.savedButtonCategories);
        categoryFile = new File(getApplicationContext().getFilesDir(), "categories.json");
        setupListeners();
    }

    private boolean textInputsFilled() {
       return !(categoryName.getText().toString().equals("")
               || categoryText1.getText().toString().equals("")
               || categoryText2.getText().toString().equals("")
               || categoryText3.getText().toString().equals(""));
    }

    private void setupListeners() {
        next.setOnClickListener(view -> {
            if (textInputsFilled()) {
                Log.i("ksi", categoryFile.getAbsolutePath());
                JSONObject jo = new JSONObject();
                JSONArray ja = new JSONArray();
                boolean fileExists = categoryFile.exists();
                if (!fileExists) {
                    try {
                        categoryFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (fileExists) {
                    Scanner scanner;
                    try {
                        scanner = new Scanner(categoryFile);
                        ja = new JSONArray(scanner.nextLine());
                    } catch (FileNotFoundException e) {
                        Log.e("Error", "Error opening saved categories file");
                        e.printStackTrace();
                        return;
                    } catch (JSONException e) {
                        Log.e("Error", "Couldn't read JSON file");
                        e.printStackTrace();
                        return;
                    }
                }

                try {
                    String catName = categoryName.getText().toString();
                    jo.put("name", catName);
                    jo.put("cat1", categoryText1.getText().toString());
                    jo.put("cat2", categoryText2.getText().toString());
                    jo.put("cat3", categoryText3.getText().toString());

                    ja.put(jo);
                    FileWriter fileWriter = new FileWriter(categoryFile, false);

                    fileWriter.write(ja.toString());
                    fileWriter.close();
                    Toast.makeText(getApplicationContext(),
                            String.format("Created new category: %s", catName), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CategoriesCustomActivity.this, CategoriesSavedActivity.class);
                    intent.putExtra("category_name", "custom");
                    startActivity(intent);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please fill in all inputs", Toast.LENGTH_SHORT).show();
            }
        });

        savedCategoriesButton.setOnClickListener(view -> {
            Intent intent = new Intent(CategoriesCustomActivity.this, CategoriesSavedActivity.class);
            if (intent != null) {
                startActivity(intent);
            }
        });
    }

}
