import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.unlam.cripto.CipherRunner;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.ciphers.mickey.Mickey;
import org.unlam.cripto.utils.Utils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration(classes = {CipherRunner.class,})
@ActiveProfiles("test")
public class UnitTests {

    @Test
    public void canEncryptAndDecrypt() {
        boolean[] K = Utils.generateRandomBooleanArray(80);
        boolean[] IV = Utils.generateRandomBooleanArray(40);

        Cipher mickey = new Mickey(K, IV);
        Cipher mickey2 = new Mickey(K, IV);

        String message = "hola";
        byte[] bytemessage = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessage = mickey.encrypt(bytemessage);
        byte[] decrypted = mickey2.encrypt(encryptedMessage);
        assertEquals(message, new String(decrypted));
    }

    @Test
    public void smallerKeyThrowException() {
        boolean[] K = Utils.generateRandomBooleanArray(40);
        boolean[] IV = Utils.generateRandomBooleanArray(40);

        assertThrows(RuntimeException.class, () -> new Mickey(K, IV));
    }

    @Test
    public void biggerKeyThrowException() {
        boolean[] K = Utils.generateRandomBooleanArray(83);
        boolean[] IV = Utils.generateRandomBooleanArray(40);

        assertThrows(RuntimeException.class, () -> new Mickey(K, IV));
    }

    @Test
    public void biggerIVThrowException() {
        boolean[] K = Utils.generateRandomBooleanArray(80);
        boolean[] IV = Utils.generateRandomBooleanArray(82);

        assertThrows(RuntimeException.class, () -> new Mickey(K, IV));
    }


}
