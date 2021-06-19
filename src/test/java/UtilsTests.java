import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.unlam.cripto.CipherRunner;
import org.unlam.cripto.utils.Utils;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@ContextConfiguration(classes = {CipherRunner.class,})
@ActiveProfiles("test")
public class UtilsTests {

    @Value("${test.key}")
    BigInteger key;

    @Value("${test.iv}")
    BigInteger iv;

    @Test
    public void utilsConvertsTobooleanArrayFromHexString() {
        assertArrayEquals(Utils.initBooleanArrayFromBinaryString("101010"), new boolean[]{true, false, true, false, true, false});
    }

    @Test
    public void utilsConvertsRightHexFromConfig() {
        assertArrayEquals(Utils.initBooleanArrayFromBinaryString(key.toString(2)), new boolean[]{true, true, true});
        assertArrayEquals(Utils.initBooleanArrayFromBinaryString(iv.toString(2)), new boolean[]{false});
    }
}
