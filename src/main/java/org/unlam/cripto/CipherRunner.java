package org.unlam.cripto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.ciphers.mickey.Mickey;
import org.unlam.cripto.utils.Utils;

import java.math.BigInteger;

@SpringBootApplication
public class CipherRunner implements CommandLineRunner {

    private String binaryKey;
    private String binaryIV;
    private String imageInput;
    private String imageEncrypted;
    private String imageDecripted;

    public CipherRunner(@Value("${ciphers.mickey.key}") BigInteger hexaKey,
                        @Value("${ciphers.mickey.iv}") BigInteger hexaIV,
                        @Value("${imageInput}") String imageInput,
                        @Value("${imageEncrypted}") String imageEncrypted,
                        @Value("${imageDecripted}") String imageDecripted) {
        this.binaryKey = hexaKey.toString(2);
        this.binaryIV = hexaIV.toString(2);
        this.imageInput = imageInput;
        this.imageEncrypted = imageEncrypted;
        this.imageDecripted = imageDecripted;
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

        byte[] bytemessage = Utils.getImageAsByteArray(imageInput);

//        System.out.print("binary message: ");
//        Utils.printByteArrayAsBinary(bytemessage);

        byte[] encryptedMessage = mickey.encrypt(bytemessage);
        Utils.saveByteArrayToFile(imageEncrypted, encryptedMessage);
//        System.out.print("encrypted:      ");
//        Utils.printByteArrayAsBinary(encryptedMessage);


        byte[] decrypted = mickey2.decrypt(encryptedMessage);
        Utils.saveByteArrayToFile(imageDecripted, decrypted);

//        System.out.print("decrypted:      ");
//        Utils.printByteArrayAsBinary(bytemessage);

    }
}
