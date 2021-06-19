package org.unlam.cripto.utils;

import java.util.BitSet;
import java.util.Random;

public class Utils {

    public static boolean[] generateRandomBooleanArray(int length) {
        Random random = new Random();
        boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = random.nextBoolean();
        }
        return array;
    }

    public static BitSet initBitSetFromString(String initializer) {
        BitSet bitset = new BitSet(initializer.length());
        for (int i = 0; i < initializer.length(); i++) {
            if (initializer.charAt(i) == '1') bitset.set(i);
        }
        return bitset;
    }
}
