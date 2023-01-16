package comp5216.sydney.edu.au.vennly;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Temporary class to represent player for testing
public class Player implements Serializable {
    public String name;
    public boolean isHost;
    public String icon;
    public Map<String, Integer> iconToId;

    public Player(String name, String icon) {
        this(name, icon, false);
    }

    public Player(String name, String icon, boolean isHost) {
        this.name = name;
        this.isHost = isHost;
        this.icon = icon;
        iconToId = new HashMap<>();
        iconToId.put("monkey", R.drawable.monkey);
        iconToId.put("snake", R.drawable.snake);
        iconToId.put("cat", R.drawable.cat);
        iconToId.put("dog", R.drawable.dog);
        iconToId.put("dolphin", R.drawable.dolphin);
        iconToId.put("rat", R.drawable.rat);
    }

    public int iconToId() {
        return iconToId.get(icon);
    }
}