package comp5216.sydney.edu.au.vennly;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;
import java.util.function.Consumer;

// Custom array adapter so that we can display buttons in join screen.
// onClick will be a callback to execute on button click.
public class JoinListAdapter extends ArrayAdapter<String> {
    private final List<String> data;
    private final Context context;
    private final Consumer<Integer> onClick;

    public JoinListAdapter(Context context, List<String> data, Consumer<Integer> onClick) {
        super(context, R.layout.list_room, data);
        this.data = data;
        this.context = context;
        this.onClick = onClick;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_room, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.joinButton = (Button) row.findViewById(R.id.lobby_button);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        String lobbyName = data.get(position);
        viewHolder.joinButton.setText(lobbyName);
        viewHolder.joinButton.setOnClickListener((v) -> onClick.accept(position));

        return row;
    }

    static class ViewHolder {
        Button joinButton;
    }

}
