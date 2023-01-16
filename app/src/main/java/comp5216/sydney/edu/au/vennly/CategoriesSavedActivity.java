package comp5216.sydney.edu.au.vennly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CategoriesSavedActivity extends AppCompatActivity {
    ListView categoriesListView;
    ArrayList<Category> categories;
    Button customCategoriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_saved);
        VennlyApplication applicationContext = (VennlyApplication) getApplicationContext();
        GameState gameState = applicationContext.gameState();

        categories = new ArrayList<>();
        CategoryColumnAdapter categoriesAdapter = new CategoryColumnAdapter(categories, getApplicationContext(), gameState::setCategory);
        categoriesListView = findViewById(R.id.buttonList);
        categoriesListView.setAdapter(categoriesAdapter);
        customCategoriesButton = findViewById(R.id.customButton);

        File categoryFile = new File(getApplicationContext().getFilesDir(), "categories.json");
        View noCategoryText = findViewById(R.id.noCategoryText);
        if (!categoryFile.exists()) {
            noCategoryText.setVisibility(View.VISIBLE);
            categoriesListView.setVisibility(View.GONE);
        } else {
            noCategoryText.setVisibility(View.GONE);
            categoriesListView.setVisibility(View.VISIBLE);
            getCategoriesFromFile();
        }

        setupListViewListener();
    }


    private void getCategoriesFromFile() {
        File categoryFile = new File(getApplicationContext().getFilesDir(), "categories.json");

        if (!categoryFile.exists()) {
            return;
        }

        Scanner scanner;
        JSONArray jsonArray;
        try {
            scanner = new Scanner(categoryFile);
            jsonArray = new JSONArray(scanner.nextLine());
        } catch (FileNotFoundException e) {
            Log.e("Error", "couldn't read category file");
            return;
        } catch (JSONException e) {
            Log.e("Error", "couldn't parse JSON");
            return;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String categoryName = (String) jsonObject.get("name");
                String cat1 = (String) jsonObject.get("cat1");
                String cat2 = (String) jsonObject.get("cat2");
                String cat3 = (String) jsonObject.get("cat3");
                categories.add(new Category(categoryName, new String[]{cat1, cat2, cat3}));
            } catch (JSONException e) {
                Log.e("Error", " couldn't parse JSON");
                return;
            }
        }
    }

    private void setupListViewListener() {
        customCategoriesButton.setOnClickListener(view -> {
            Intent intent = new Intent(CategoriesSavedActivity.this, CategoriesCustomActivity.class);
            if (intent != null) {
                startActivity(intent);
            }
        });
    }
}
