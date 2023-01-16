package comp5216.sydney.edu.au.vennly.bluetooth;

import comp5216.sydney.edu.au.vennly.AnsweredPrompt;

public class PromptMessage extends BluetoothMessage {
    private AnsweredPrompt prompt;

    public PromptMessage(AnsweredPrompt prompt) {
        super();
        this.prompt = prompt;
    }

    public PromptMessage(String sender, AnsweredPrompt prompt) {
        super(sender);
        this.prompt = prompt;
    }

    public AnsweredPrompt getPrompt() {
        return prompt;
    }

    public void setPrompt(AnsweredPrompt prompt) {
        this.prompt = prompt;
    }
}
