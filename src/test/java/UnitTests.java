import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import org.unlam.cripto.CipherRunner;
import org.unlam.cripto.ciphers.Cipher;
import org.unlam.cripto.ciphers.mickey.Mickey;
import org.unlam.cripto.utils.Utils;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@ContextConfiguration(classes = {CipherRunner.class,})
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
        Assert.isTrue(new String(decrypted).equals(message));
    }



}
