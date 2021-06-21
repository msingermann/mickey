package org.unlam.cripto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.ciphers.mickey.Mickey;
import org.unlam.cripto.utils.Utils;

import java.math.BigInteger;
import java.util.Scanner;

@SpringBootApplication
public class CipherRunner implements CommandLineRunner {

    private String binaryKey;
    private String binaryIV;
    private String imageInput;
    private String imageEncrypted;
    private String imageDecripted;
    private String txtEncrypted;
    private String txtDecripted;

    private Scanner sc = new Scanner(System.in); 
    private String strAnt = ""; 

    public CipherRunner(@Value("${ciphers.mickey.key}") BigInteger hexaKey,
                        @Value("${ciphers.mickey.iv}") BigInteger hexaIV,
                        @Value("${imageInput}") String imageInput,
                        @Value("${imageEncrypted}") String imageEncrypted,
                        @Value("${imageDecripted}") String imageDecripted,
                        @Value("${txtEncrypted}") String txtEncrypted,
                        @Value("${txtDecripted}") String txtDecripted                        
                        ) {
        this.binaryKey = hexaKey.toString(2);
        this.binaryIV = hexaIV.toString(2);
        this.imageInput = imageInput;
        this.imageEncrypted = imageEncrypted;
        this.imageDecripted = imageDecripted;
        this.txtEncrypted = txtEncrypted;
        this.txtDecripted = txtDecripted;
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

        String str;        
        System.out.print("¿Qué desea encriptar?\n 1-Texto\n 2-Caracter a Caracter \n 3-Imagen\n Ingrese Opcion: ");  

        str= sc.nextLine();     

        if (str.equals("1")){ // TEXTO
            System.out.print("Ingrese Texto a Encryptar: ");  
            str = sc.nextLine();    
            //System.out.print("Encriptando: " + str + "\n"); 

            byte[] bytemessage = str.getBytes();

            //System.out.print("guardando en: " + txtEncrypted);

            byte[] encryptedMessage = mickey.encrypt(bytemessage);            
            //System.out.print("encrypted: ");
            Utils.saveByteArrayToFile(txtEncrypted, encryptedMessage);
            
            byte[] decrypted = mickey2.decrypt(encryptedMessage);
            Utils.saveByteArrayToFile(txtDecripted, decrypted);
        }
        else
        {
            if (str.equals("2")){ // TEXTO - CARACTER A CARACTER                
                byte[] encryptedMessage = null;

                str = ingresaCaracter();

                while(!str.equals("*")){
                    System.out.print("TEXTO: " + str.toString() + "\n");
                    strAnt = strAnt + str;
                    System.out.print("TEXTO: " + strAnt.toString() + "\n");

                    byte[] bytemessage = strAnt.getBytes();
                    encryptedMessage = mickey.encrypt(bytemessage);  
                    System.out.print("Texto a encriptar: " + strAnt + "\n");
                    System.out.print("Texto a encriptado: " + encryptedMessage + "\n");

                    Utils.saveByteArrayToFile(txtEncrypted, encryptedMessage);

                    str = ingresaCaracter();
                }
                
                byte[] decrypted = mickey2.decrypt(encryptedMessage);
                Utils.saveByteArrayToFile(txtDecripted, decrypted);
            }
            else
            {
                if (str.equals("3")){ // IMAGEN
                    byte[] bytemessage = Utils.getImageAsByteArray(imageInput);

                    byte[] encryptedMessage = mickey.encrypt(bytemessage);
                    Utils.saveByteArrayToFile(imageEncrypted, encryptedMessage);
                    Utils.saveByteArrayToFile(txtEncrypted, encryptedMessage);

                    byte[] decrypted = mickey2.decrypt(encryptedMessage);
                    Utils.saveByteArrayToFile(imageDecripted, decrypted);

                }
            }
        }      
    }
    private String ingresaCaracter(){
        String str = "";
        System.out.print("Ingrese Caracter (* para finalizar): ");  
        str = sc.nextLine();                  
        
        return str;
    }
}
