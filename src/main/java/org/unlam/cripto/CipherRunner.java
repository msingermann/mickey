package org.unlam.cripto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.unlam.cripto.ciphers.mickey.MickeyImpl;

import java.util.BitSet;
import java.util.Random;

@SpringBootApplication
public class CipherRunner implements CommandLineRunner {

    /**
     * Spring boot application bootstrap. Application main method.
     *
     * @param args JVM command arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(CipherRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        BitSet K = generateRandomBitSet(80);
        BitSet IV = generateRandomBitSet(40);

        MickeyImpl mickey = new MickeyImpl(K, IV);

//        mickey.generateKeyStream();
//
//        mickey.encrypt("hola");


    }

    public BitSet generateRandomBitSet(int length) {
        Random random = new Random();
        BitSet bitSet = new BitSet(length);
        for (int i = 0; i < length; i++) {
            bitSet.set(i, random.nextBoolean());
        }
        return bitSet;
    }
}
