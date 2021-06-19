package org.unlam.cripto.ciphers.mickey;

import org.unlam.cripto.ciphers.Cipher;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class MickeyImpl implements Cipher {
    private static final int LENGTH = 80;
    private static final String STRING_COMP_0 = "00001100010111101001010101010110100100000001010101000010100111100101011111111100";
    private static final String STRING_COMP_1 = "01011001011110010100011010111011110001101011100001000101110001111110101110111100";
    private static final String STRING_FB_0 = "11110101111111100101111111111001100000011100100101010010111101010100000000011010";
    private static final String STRING_FB_1 = "11101110000111010011000100110010110001100000110110001000100100101101010010100011";

    private final BitSet COMP0 = initRegister(STRING_COMP_0);
    private final BitSet COMP1 = initRegister(STRING_COMP_1);
    private final BitSet FB0 = initRegister(STRING_FB_0);
    private final BitSet FB1 = initRegister(STRING_FB_1);

    private List<Integer> RTAPS = Arrays.asList(new Integer[]{0, 2, 4, 6, 7, 8, 9, 13, 14, 16, 17, 20, 22, 24, 26, 27, 28, 34, 35, 37, 39, 41, 43, 49, 51, 52, 54, 56, 62, 67, 69, 71, 73, 76, 78, 79});

    private BitSet R = new BitSet(LENGTH);
    private BitSet S = new BitSet(LENGTH);

    public MickeyImpl(boolean[] K, boolean[] IV) {
        load_iv(IV);
        load_k(K);
        preclock();
    }

    private void load_iv(boolean[] iv) {
        for (int i = 0; i < iv.length; i++) {
            clock_kg(true, iv[i]);
        }
    }

    private void load_k(boolean[] k) {
        for (int i = 0; i < LENGTH; i++) {
            clock_kg(true, k[i]);
        }
    }

    private void preclock() {
        for (int i = 0; i < LENGTH; i++) {
            clock_kg(true, false);
        }
    }

    public boolean generateKeyStream() {
        boolean z = xor(R.get(0), S.get(0));
        clock_kg(false, false);
        return z;
    }

    private boolean xor(boolean a, boolean b) {
        return (a && !b) || (!a && b);
    }

    private BitSet clock_r(BitSet R, boolean inputBitR, boolean controlBitR) {
        boolean feedbackBit = xor(R.get(LENGTH - 1), inputBitR);
        BitSet clockedR = new BitSet(LENGTH);
        clockedR.clear(0);
        for (int i = 1; i < LENGTH - 1; i++) {
            if (R.get(i - 1)) clockedR.set(i);
        }
        for (int i = 0; i < LENGTH; i++) {
            if (RTAPS.contains(i)) clockedR.set(i, xor(clockedR.get(i), feedbackBit));
        }
        if (controlBitR) {
            for (int i = 0; i < LENGTH; i++) {
                clockedR.set(i, xor(clockedR.get(i), R.get(i)));
            }
        }
        return clockedR;
    }

    private BitSet clock_s(BitSet S, boolean inputBitS, boolean controlBitS) {
        boolean feedbackBit = xor(S.get(LENGTH - 1), inputBitS);
        BitSet clockedS = new BitSet(LENGTH);
        clockedS.clear(0);
        for (int i = 1; i < LENGTH - 1; i++) {
            clockedS.set(i, xor(S.get(i - 1), (xor(S.get(i), COMP0.get(i)) || xor(S.get(i - 1), COMP1.get(i)))));
        }
        clockedS.set(LENGTH - 1, S.get(LENGTH - 2));
        if(controlBitS) {
            for (int i = 0; i < LENGTH; i++) {
                clockedS.set(i, xor(clockedS.get(i), (FB0.get(i) || feedbackBit)));
            }
        } else {
            for (int i = 0; i < LENGTH; i++) {
                clockedS.set(i, xor(clockedS.get(i), (FB1.get(i) || feedbackBit)));
            }
        }
        return clockedS;
    }

    private void clock_kg(boolean mixing, boolean inputBit) {
        boolean controlBitR = xor(S.get(27), R.get(53));
        boolean controlBitS = xor(S.get(53), R.get(26));

        boolean inputBitR = mixing ? xor(inputBit, S.get(40)) : inputBit;
        boolean inputBitS = inputBit;

        R = clock_r(R, inputBitR, controlBitR);
        S = clock_s(S, inputBitS, controlBitS);
    }

    public static BitSet initRegister(String initializer) {
        BitSet bitset = new BitSet(initializer.length());
        for (int i = 0; i < initializer.length(); i++) {
            if (initializer.charAt(i) == '1') bitset.set(i);
        }
        return bitset;
    }

    @Override
    public byte[] encrypt(byte[] message) {
        BitSet messageBitSet = BitSet.valueOf(message);
        BitSet encryptedBitSet = new BitSet();
        for (int i = 0; i < message.length * 8; i++) {
            encryptedBitSet.set(i, xor(messageBitSet.get(i), this.generateKeyStream()));
        }
        return encryptedBitSet.toByteArray();
    }

}
