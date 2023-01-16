package comp5216.sydney.edu.au.vennly;

import java.io.Serializable;
import java.util.ArrayList;

public class Prompt implements Serializable {
    private final String promptTitle;
    private final ArrayList<String> prompts;

    public Prompt(String promptTitle, ArrayList<String> prompts){
        this.promptTitle = promptTitle;
        this.prompts = prompts;

    }

    public String getPromptTitle() {
        return promptTitle;
    }

    public ArrayList<String> getPrompts(){return prompts;}
}
