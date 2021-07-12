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

    private final static int HEADERS_LENGHT = 54 ;
    private String binaryKey;
    private String binaryIV;
    private String imageInput;
    private String imageEncrypted;

    public CipherRunner(@Value("${ciphers.mickey.key}") BigInteger hexaKey,
                        @Value("${ciphers.mickey.iv}") BigInteger hexaIV,
                        @Value("${imageInput}") String imageInput,
                        @Value("${imageEncrypted}") String imageEncrypted) {
        this.binaryKey = hexaKey.toString(2);
        this.binaryIV = hexaIV.toString(2);
        this.imageInput = imageInput;
        this.imageEncrypted = imageEncrypted;
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

        byte[] bytemessage = Utils.getImageAsByteArray(imageInput);
        byte[] headers = new byte[HEADERS_LENGHT];
        byte[] imagen = new byte[bytemessage.length - HEADERS_LENGHT];

        for (int i = 0; i < HEADERS_LENGHT; i++) {
            headers[i] = bytemessage[i];
        }
        for (int i = 0; i < imagen.length; i++) {
            imagen[i] = bytemessage[i + HEADERS_LENGHT];
        }

        byte[] encryptedImage = mickey.encrypt(imagen);
        byte[] encriptedImageWithHeader = new byte[encryptedImage.length + HEADERS_LENGHT];


        for (int i = 0; i < HEADERS_LENGHT; i++) {
            encriptedImageWithHeader[i] = headers[i];
        }
        for (int i = 0; i < encryptedImage.length; i++) {
            encriptedImageWithHeader[i + HEADERS_LENGHT] = encryptedImage[i];
        }

        Utils.saveByteArrayToFile(imageEncrypted, encriptedImageWithHeader);

    }
}
