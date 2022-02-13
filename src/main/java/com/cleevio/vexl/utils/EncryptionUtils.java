package com.cleevio.vexl.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
public class EncryptionUtils {

    public byte[] decodeBase64String(String value) {
        return Base64.getDecoder().decode(value);
    }

    public byte[] createHash(String phoneNumber, String hashFunction)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(hashFunction);
        return digest.digest(phoneNumber.getBytes(StandardCharsets.UTF_8));
    }

    public String encodeToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public PublicKey createPublicKey(String base64PublicKey, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedPublicBytes = Base64.getDecoder().decode(base64PublicKey);
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(decodedPublicBytes));
    }

    public PrivateKey createPrivateKey(String base64PrivateKey, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedPrivateBytes = Base64.getDecoder().decode(base64PrivateKey);
        return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(decodedPrivateBytes));
    }

    public KeyPair retrieveKeyPair(String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
        return kpg.generateKeyPair();
    }
}
