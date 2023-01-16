package comp5216.sydney.edu.au.vennly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;
import java.util.function.Consumer;

public class PromptListAdapter extends ArrayAdapter<Prompt> {
    private final Context context;
    private final List<Prompt> data;
    private final Consumer<Prompt> onPromptClick;
    public PromptListAdapter(Context context, List<Prompt> prompts, Consumer<Prompt> onPromptClick) {
        super(context, R.layout.list_saved_prompt, prompts);
        this.data = prompts;
        this.context = context;
        this.onPromptClick = onPromptClick;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_saved_prompt, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.promptButton = (Button) row.findViewById(R.id.promptButton);


            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        Prompt prompt = data.get(position);
        viewHolder.promptButton.setText(prompt.getPromptTitle());
        viewHolder.promptButton.setOnClickListener(view -> {
            onPromptClick.accept(prompt);
            Intent intent = new Intent(context, GameplayActivity.class);
            intent.putExtra("promptList", prompt.getPrompts());
            context.startActivity(intent);
        });


        return row;
    }


    class ViewHolder{
        Button promptButton;

    }
}
