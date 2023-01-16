package comp5216.sydney.edu.au.vennly;

import android.util.Size;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.vennly.bluetooth.RoundAnswersMessage;

public class CompressionTest {

    private List<Map<String, AnsweredPrompt>> generateRoundAnswers(int numPlayers, int numPrompts, int promptSize) {
        String promptColour = "red";
        String promptName = String.join("", Collections.nCopies(promptSize, "a"));

        List<Map<String, AnsweredPrompt>> roundAnswers = new ArrayList<>();

        for (int i = 0; i < numPrompts; i++) {
            Map<String, AnsweredPrompt> map = new HashMap<>();
            for (int j = 0; j < numPlayers; j++)  {
                AnsweredPrompt prompt = new AnsweredPrompt(promptName, 0, 0);
                prompt.setColour(promptColour);
                map.put(String.valueOf(i), prompt);
            }
            roundAnswers.add(map);
        }

        return roundAnswers;
    }

    @Test
    public void testSizes() {
        int[] playerSizes = {5};
        int[] numPrompts = {1, 2, 3, 4 ,5, 6, 7, 8, 9, 10};

        GameState gameState = new GameState();

        for (int playerSize : playerSizes) {
            for (int promptSize : numPrompts) {
                List<Map<String, AnsweredPrompt>> answers = generateRoundAnswers(playerSize, promptSize, 3);
                gameState.setRoundAnswers(answers);
                RoundAnswersMessage messageNormal = new RoundAnswersMessage(gameState, Constants.SERVER_ID, false);
                RoundAnswersMessage messageCompress = new RoundAnswersMessage(gameState, Constants.SERVER_ID, true);

                System.out.printf("Player Size: %d | Num Prompts: %d\n", playerSize, promptSize);
                int normalSize = SizeEstimator.estimateSize(messageNormal);
                int compressSize = SizeEstimator.estimateSize(messageCompress);
                System.out.printf("normal: %d | compressed: %d\n", normalSize, compressSize);
            }
        }
    }

}
