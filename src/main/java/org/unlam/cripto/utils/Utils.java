package org.unlam.cripto.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static boolean[] initBooleanArrayFromBinaryString(String initializer) {
        boolean[] booleanArray = new boolean[initializer.length()];
        initializer.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < initializer.length(); i++) {
            if (initializer.charAt(i) == '1') booleanArray[i] = true;
        }
        return booleanArray;
    }

    public static byte[] getImageAsByteArray(String path) throws IOException {
        File file = new File(path);
        return FileUtils.readFileToByteArray(file);
    }

    public static void saveByteArrayToFile(String path, byte[] data) throws IOException {
        File file = new File(path);
        FileUtils.writeByteArrayToFile(file, data);
    }
}
