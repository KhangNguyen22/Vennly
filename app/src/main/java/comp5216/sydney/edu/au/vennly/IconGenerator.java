package comp5216.sydney.edu.au.vennly;

import java.util.Random;

public class IconGenerator {
    private final String[] icons = {"monkey", "snake", "dog", "cat", "dolphin", "rat"};
    private int currentIndex = 0;
    private int[] randomSequence;

    public IconGenerator() {
        // generates random sequence of numbers from 0 - icons.length - 1.
        randomSequence = genRandomSequence();
    }

    public String nextIcon() {
        String icon = icons[randomSequence[currentIndex++]];

        if (currentIndex >= icons.length) {
            randomSequence = genRandomSequence();
            currentIndex = 0;
        }

        return icon;
    }

    public int[] genRandomSequence() {
        return new Random().ints(0, icons.length).distinct().limit(icons.length).toArray();
    }
}
