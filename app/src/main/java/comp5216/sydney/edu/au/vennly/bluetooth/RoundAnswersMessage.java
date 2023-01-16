package comp5216.sydney.edu.au.vennly.bluetooth;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import comp5216.sydney.edu.au.vennly.AnsweredPrompt;
import comp5216.sydney.edu.au.vennly.GameState;

public class RoundAnswersMessage extends BluetoothMessage {
    private List<List<String>> names;
    private List<List<AnsweredPrompt>> answers;
    private byte[] compressedNames;
    private byte[] compressedAnswers;
    private boolean compressFlag;

    public RoundAnswersMessage(GameState gameState, String sender, boolean compressFlag) {
        super(sender);
        this.compressFlag = compressFlag;
        List<Map<String, AnsweredPrompt>> roundAnswers = gameState.getRoundAnswers();

        names = new ArrayList<>();
        answers = new ArrayList<>();
        for (Map<String, AnsweredPrompt> round : roundAnswers) {
            ArrayList<String> roundNames = new ArrayList<>();
            ArrayList<AnsweredPrompt> roundPrompts = new ArrayList<>();
            for (Map.Entry<String, AnsweredPrompt> answer : round.entrySet()) {
                roundNames.add(gameState.getNameFromUUID(answer.getKey()));
                roundPrompts.add(answer.getValue());
            }
            names.add(roundNames);
            answers.add(roundPrompts);
        }

        if (compressFlag) {
            compressedNames = compressObject(names);
            compressedAnswers = compressObject(answers);

            names = null;
            answers = null;
        }
    }

    public List<Map<String, AnsweredPrompt>> getRoundAnswers() {
        if (compressFlag) {
            names = (List<List<String>>) decompressObject(compressedNames);
            answers = (List<List<AnsweredPrompt>>) decompressObject(compressedAnswers);
        }

        List<Map<String, AnsweredPrompt>> roundAnswers = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Map<String, AnsweredPrompt> map = new HashMap<>();
            List<String> roundNames = names.get(i);
            List<AnsweredPrompt> roundPrompts = answers.get(i);

            for (int j = 0; j < roundNames.size(); j++) {
                map.put(roundNames.get(j), roundPrompts.get(j));
            }
            roundAnswers.add(map);
        }
        return roundAnswers;
    }

    // Citation acknowledgement: https://stackoverflow.com/questions/5934495/implementing-in-memory-compression-for-objects-in-java
    private byte[] compressObject(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
            objectOut.writeObject(obj);
            objectOut.close();
            byte[] bytes =  baos.toByteArray();
            return bytes;
        } catch (IOException e) {
            Log.e("game state message","Compress failed");
            return new byte[0];
        }
    }

    // Citation acknowledgement: https://stackoverflow.com/questions/5934495/implementing-in-memory-compression-for-objects-in-java
    private Object decompressObject(byte[] compressedRoundAnswers) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedRoundAnswers);
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            Log.e("game state message","Decompression failed");
            e.printStackTrace();
            return null;
        }
    }
}
