package org.unlam.cripto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.ciphers.mickey.Mickey;
import org.unlam.cripto.utils.Utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class CipherRunner implements CommandLineRunner {

    private String binaryKey;
    private String binaryIV;

    public CipherRunner(@Value("${ciphers.mickey.key}") BigInteger hexaKey,
                        @Value("${ciphers.mickey.iv}") BigInteger hexaIV) {
        this.binaryKey = hexaKey.toString(2);
        this.binaryIV = hexaIV.toString(2);
    }

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

        boolean[] K = Utils.initBooleanArrayFromBinaryString(binaryKey);
        boolean[] IV = Utils.initBooleanArrayFromBinaryString(binaryIV);

        Cipher mickey = new Mickey(K, IV);
        Cipher mickey2 = new Mickey(K, IV);

        String message = "hola";
        System.out.println("ascii message: " + message);

        byte[] bytemessage = message.getBytes(StandardCharsets.UTF_8);
        System.out.print("binary message: ");
        Utils.printByteArrayAsBinary(bytemessage);

        byte[] encryptedMessage = mickey.encrypt(bytemessage);
        System.out.print("encrypted:      ");
        Utils.printByteArrayAsBinary(encryptedMessage);


        byte[] decrypted = mickey2.decrypt(encryptedMessage);

        System.out.print("decrypted:      ");
        Utils.printByteArrayAsBinary(bytemessage);

        System.out.println("ascii decrypted: " + new String(decrypted));

    }
}
