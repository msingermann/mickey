package org.unlam.cripto.ciphers;

public interface Cipher {

    byte[] encrypt(byte[] message);

    byte[] decrypt(byte[] message);

}
