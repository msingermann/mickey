package org.unlam.cripto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.unlam.cripto.ciphers.mickey.MickeyImpl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

        boolean[] K = generateRandomBooleanArray(80);
        boolean[] IV = generateRandomBooleanArray(40);

        MickeyImpl mickey = new MickeyImpl(K, IV);
        MickeyImpl mickey2 = new MickeyImpl(K, IV);

        String message = "hola";
        byte[] bytemessage = message.getBytes(StandardCharsets.UTF_8);

        byte[] encryptedMessage = mickey.encrypt(bytemessage);

        System.out.println("encrypted: " + Arrays.toString(encryptedMessage));

        byte[] decrypted = mickey2.encrypt(encryptedMessage);

        System.out.println("decrypted: " + new String(decrypted));

    }

    public boolean[] generateRandomBooleanArray(int length) {
        Random random = new Random();
        boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = random.nextBoolean();
        }
        return array;
    }
}
