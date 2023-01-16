package comp5216.sydney.edu.au.vennly;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

// Custom array adapter so that we can display players in lobby screen
public class PlayerListAdapter extends ArrayAdapter<Player> {

    private final Context context;
    private final List<Player> data;
    // id of player displaying list.
    private final String playerID;

    public PlayerListAdapter(Context context, List<Player> players, String playerID) {
        super(context, R.layout.list_lobby_player, players);
        this.data = players;
        this.context = context;
        // this should be true if device displaying the list is the game host
        this.playerID = playerID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_lobby_player, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.playerIcon = (ImageView) row.findViewById(R.id.player_icon);
            viewHolder.playerName = (TextView) row.findViewById(R.id.player_name);
            viewHolder.hostIcon = (ImageView) row.findViewById(R.id.host_icon);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        Player player = data.get(position);

        viewHolder.playerName.setText(player.name);
        if (player.name.equals(playerID)) {
            viewHolder.playerName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }

        viewHolder.playerIcon.setImageResource(player.iconToId());

        if (player.isHost) {
            viewHolder.hostIcon.setVisibility(View.VISIBLE);
        }

        return row;
    }

    class ViewHolder {
        ImageView playerIcon;
        TextView playerName;
        ImageView hostIcon;
    }

}
