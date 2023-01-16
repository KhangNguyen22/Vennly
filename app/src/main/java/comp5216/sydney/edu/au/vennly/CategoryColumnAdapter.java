package comp5216.sydney.edu.au.vennly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CategoryColumnAdapter extends ArrayAdapter<Category> {
    Context context;
    Consumer<Category> onCategoryClick;

    private static class CategoryViewData {
        TextView categoryName;
    }

    public CategoryColumnAdapter(ArrayList<Category> categories, Context context, Consumer<Category> onCategoryClick) {
        super(context, R.layout.button_list_item, categories);
        this.context = context;
        this.onCategoryClick = onCategoryClick;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.button_list_item, parent, false);

        CategoryViewData categoryViewData = new CategoryViewData();

        categoryViewData.categoryName = convertView.findViewById(R.id.textBoxButton);
        convertView.setTag(categoryViewData);
        categoryViewData.categoryName.setText(category.getCategoryName());
        categoryViewData.categoryName.setOnClickListener(view -> {
            onCategoryClick.accept(category);
            Intent intent = new Intent(context, PromptsSavedActivity.class);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
