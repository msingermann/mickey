import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import org.unlam.cripto.CipherRunner;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.ciphers.mickey.MickeyImpl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

@SpringBootTest
@ContextConfiguration(classes = {CipherRunner.class,})
public class UnitTests {

    @Test
    public void canEncryptAndDecrypt() {
        boolean[] K = generateRandomBooleanArray(80);
        boolean[] IV = generateRandomBooleanArray(40);

        Cipher mickey = new MickeyImpl(K, IV);
        Cipher mickey2 = new MickeyImpl(K, IV);

        String message = "hola";
        byte[] bytemessage = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessage = mickey.encrypt(bytemessage);
        byte[] decrypted = mickey2.encrypt(encryptedMessage);
        Assert.isTrue(new String(decrypted).equals(message));
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
