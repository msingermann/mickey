package org.unlam.cripto.ciphers;

public interface Cipher {

    /**
     * Encrypt a message.
     *
     * @param message
     * @return encrypted bytes.
     */
    byte[] encrypt(byte[] message);

    /**
     * Decrypts a message.
     *
     * @param message
     * @return decripted bytes.
     */
    byte[] decrypt(byte[] message);

}
