package comp5216.sydney.edu.au.vennly;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class GameState implements Serializable {
    private String lobbyName;
    private Prompt prompt;
    private Category category;
    private Map<String, String> playerToIcon;

    private List<String> players;
    public List<Map<String, AnsweredPrompt>> roundAnswers;
    // only for host who will store player names as uuids.
    private Map<String, String> uuidToName;

    public GameState() {
        reset();
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String lobbyName() {
        return lobbyName;
    }

    public void addPlayer(String player, String icon) {
        players.add(player);
        playerToIcon.put(player, icon);
    }

    public void addPlayer(String uuid, String name, String icon) {
        synchronized (players) {
            players.add(uuid);
            playerToIcon.put(name, icon);
            uuidToName.put(uuid, name);
        }
    }

    public List<String> getPlayers() {
        return players;
    }

    public void clearPlayers() {
        players = new ArrayList<>();
    }

    public String getNameFromUUID(String uuid) {
        return uuidToName.get(uuid);
    }

    public void startNewRound() {
        roundAnswers.add(new HashMap<>());
    }

    public List<Map<String, AnsweredPrompt>> getRoundAnswers() {
        return roundAnswers;
    }

    public void setRoundAnswers(List<Map<String, AnsweredPrompt>> roundAnswers) {
        this.roundAnswers = roundAnswers;
    }

    public int numPlayersAnswersCurrentRound() {
        int currentRound = roundAnswers.size() - 1;
        return roundAnswers.get(currentRound).size();
    }

    public void addPlayerAnswer(String player, AnsweredPrompt answer) {
        Log.i("answer", String.format("%s, %s", player, answer.getName()));
        synchronized (roundAnswers) {
            int currentRound = roundAnswers.size() - 1;
            roundAnswers.get(currentRound).put(player, answer);
        }
    }

    public Map<String, String> playerToIcon() {
        return playerToIcon;
    }

    public Map<String, String> uuidToName() {
        return uuidToName;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
    }

    public Prompt getPrompt() {
        return this.prompt;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return this.category;
    }

    public void reset() {
        players = new ArrayList<>();
        roundAnswers = new ArrayList<>();
        uuidToName = new HashMap<>();
        playerToIcon = new HashMap<>();
    }


}
