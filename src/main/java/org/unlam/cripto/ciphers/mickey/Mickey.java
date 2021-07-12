package org.unlam.cripto.ciphers.mickey;

import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.utils.Utils;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Mickey cipher based on "The stream cipher MICKEY (version 1) - Algorithm specification issue 1.0" (https://www.ecrypt.eu.org/stream/ciphers/mickey/mickey.pdf)
 */
public class Mickey implements Cipher {

    private static final Logger LOGGER = LogManager.getLogger(Mickey.class);

    /**
     * In Mickey Phase 1 the length is 80 bits for the registers and key.
     */
    private static final int FIXED_KEY_LENGTH = 80;
    private static final String STRING_COMP_0 = "00001100010111101001010101010110100100000001010101000010100111100101011111111100";
    private static final String STRING_COMP_1 = "01011001011110010100011010111011110001101011100001000101110001111110101110111100";
    private static final String STRING_FB_0 = "11110101111111100101111111111001100000011100100101010010111101010100000000011010";
    private static final String STRING_FB_1 = "11101110000111010011000100110010110001100000110110001000100100101101010010100011";
    private static final List<Integer> RTAPS = Arrays.asList(new Integer[]{0, 2, 4, 6, 7, 8, 9, 13, 14, 16, 17, 20, 22, 24, 26, 27, 28, 34, 35, 37, 39, 41, 43, 49, 51, 52, 54, 56, 62, 67, 69, 71, 73, 76, 78, 79});

    private final BitSet COMP0 = Utils.initBitSetFromString(STRING_COMP_0);
    private final BitSet COMP1 = Utils.initBitSetFromString(STRING_COMP_1);
    private final BitSet FB0 = Utils.initBitSetFromString(STRING_FB_0);
    private final BitSet FB1 = Utils.initBitSetFromString(STRING_FB_1);

    /**
     * R registry.
     */
    private BitSet R = new BitSet(FIXED_KEY_LENGTH);

    /**
     * S registry.
     */
    private BitSet S = new BitSet(FIXED_KEY_LENGTH);

    public Mickey(boolean[] K, boolean[] IV) {
        Stopwatch sw = Stopwatch.createStarted();
        if (K.length != FIXED_KEY_LENGTH || IV.length > FIXED_KEY_LENGTH) {
            throw new RuntimeException("Key and Initialization vector error");
        }

        load_array(IV);
        load_array(K);
        preclock();
        LOGGER.info("Mickey Initialized in: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Loads a vector clocking the registers with it.
     *
     * @param array
     */
    private void load_array(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            clock_kg(true, array[i]);
        }
    }

    /**
     * Performs the preclock routine.
     */
    private void preclock() {
        for (int i = 0; i < FIXED_KEY_LENGTH; i++) {
            clock_kg(true, false);
        }
    }

    /**
     * Gets one keystream bit and clock the registers to be ready for the next one.
     *
     * @return keystream bit.
     */
    public boolean generateKeyStream() {
        boolean z = R.get(0) ^ S.get(0);
        clock_kg(false, false);
        return z;
    }

    /**
     * Clocks the R registry.
     *
     * @param R
     * @param inputBitR
     * @param controlBitR
     * @return clocked R registry.
     */
    private BitSet clock_r(BitSet R, boolean inputBitR, boolean controlBitR) {
        boolean feedbackBit = R.get(FIXED_KEY_LENGTH - 1) ^ inputBitR;
        BitSet clockedR = new BitSet(FIXED_KEY_LENGTH);
        for (int i = 1; i < FIXED_KEY_LENGTH; i++) {
            clockedR.set(i, R.get(i - 1));
        }
        clockedR.clear(0);
        for (int i = 0; i < FIXED_KEY_LENGTH; i++) {
            if (RTAPS.contains(i)) clockedR.set(i, clockedR.get(i) ^ feedbackBit);
        }
        if (controlBitR) {
            for (int i = 0; i < FIXED_KEY_LENGTH; i++) {
                clockedR.set(i, clockedR.get(i) ^ R.get(i));
            }
        }
        return clockedR;
    }

    /**
     * Clocks the S register.
     *
     * @param S
     * @param inputBitS
     * @param controlBitS
     * @return clocked S registry.
     */
    private BitSet clock_s(BitSet S, boolean inputBitS, boolean controlBitS) {
        boolean feedbackBit = S.get(FIXED_KEY_LENGTH - 1) ^ inputBitS;
        BitSet tempS = new BitSet(FIXED_KEY_LENGTH);
        BitSet clockedS = new BitSet(FIXED_KEY_LENGTH);
        for (int i = 1; i < FIXED_KEY_LENGTH - 1; i++) {
            tempS.set(i, S.get(i - 1) ^ ((S.get(i) ^ COMP0.get(i)) || (S.get(i + 1) ^ COMP1.get(i))));
        }
        tempS.clear(0);
        tempS.set(79, S.get(78));

        if (controlBitS) {
            for (int i = 0; i < FIXED_KEY_LENGTH; i++) {
                clockedS.set(i, tempS.get(i) ^ (FB1.get(i) || feedbackBit));
            }
        } else {
            for (int i = 0; i < FIXED_KEY_LENGTH; i++) {
                clockedS.set(i, tempS.get(i) ^ (FB0.get(i) || feedbackBit));
            }
        }
        return clockedS;
    }

    /**
     * Clocks registries.
     *
     * @param mixing
     * @param inputBit
     */
    private void clock_kg(boolean mixing, boolean inputBit) {
        boolean controlBitR = S.get(27) ^ R.get(53);
        boolean controlBitS = S.get(53) ^ R.get(26);

        boolean inputBitR = mixing ? inputBit ^ S.get(40) : inputBit;
        boolean inputBitS = inputBit;

        R = clock_r(R, inputBitR, controlBitR);
        S = clock_s(S, inputBitS, controlBitS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encrypt(byte[] message) {
        Stopwatch sw = Stopwatch.createStarted();
        BitSet messageBitSet = BitSet.valueOf(message);
        BitSet encryptedBitSet = new BitSet();
        for (int i = 0; i < message.length * 8; i++) {
            encryptedBitSet.set(i, messageBitSet.get(i) ^ this.generateKeyStream());
            if(i != 0 && i % 500000 == 0) {
                LOGGER.info( String.format("%.02f", Double.valueOf(i) / (message.length*8) *100) + "%");
            }
        }
        long time = sw.elapsed(TimeUnit.MILLISECONDS);
        LOGGER.info("Process finished. took: {}ms Speed: {} Kb/seg ", time, message.length*8/time);
        return encryptedBitSet.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decrypt(byte[] message) {
        return encrypt(message);
    }

}
