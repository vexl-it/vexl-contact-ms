package com.cleevio.vexl.common.cryptolib;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CLibrary extends Library {

    String LIBRARY_VERSION = "-v0-0-15";
    String LIBRARY_NAME = "libvc";
    String LIBRARY_NAME_LINUX = "vc";
    String FULL_NAME_WIN = LIBRARY_NAME + LIBRARY_VERSION;
    String FULL_NAME_LINUX = LIBRARY_NAME_LINUX + LIBRARY_VERSION;
    String PATH_LINUX = "src/main/resources/linux-x86-64/" + LIBRARY_NAME + LIBRARY_VERSION + ".so";

    CLibrary CRYPTO_LIB = Native.load(Platform.isWindows() ? FULL_NAME_WIN : FULL_NAME_LINUX, CLibrary.class);

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
