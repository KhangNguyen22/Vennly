package comp5216.sydney.edu.au.vennly;

import java.io.Serializable;

public class AnsweredPrompt implements Serializable {
    String name; //The prompt
    double x; //It's location
    double y;
    String colour; //What categories it was put in to. Eg, Green would be cat 1 & 2


    public AnsweredPrompt(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return this.colour;
    }
}
