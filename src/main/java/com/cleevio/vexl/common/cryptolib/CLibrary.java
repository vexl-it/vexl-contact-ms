package com.cleevio.vexl.common.cryptolib;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CLibrary extends Library {

    CLibrary CRYPTO_LIB = Native.load(Platform.isWindows() ? "libvc" : "vc", CLibrary.class);

    /**
     * ECDSA
     */
    boolean ecdsa_verify(String base64_public_key, String data, int data_len, String base64_signature);

    String ecdsa_sign(String base64_public_key, String base64_private_key, String data, int data_len);

    /**
     * ECIES
     */
    String ecies_encrypt(String base64_public_key, String message);

    String ecies_decrypt(String base64_public_key, String base64_private_key, String encoded_cipher);

    /**
     * SHA-256
     */
    String sha256_hash(String data, int data_len);

    /**
     * AES
     */
    String aes_encrypt(String password, String message);

    String aes_decrypt(String password, String cipher);

    /**
     * HMAC
     */
     String hmac_digest(String password, String message);

     boolean hmac_verify(String password, String message, String digest);
}
