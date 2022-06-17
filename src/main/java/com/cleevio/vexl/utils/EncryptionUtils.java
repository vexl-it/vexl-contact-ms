package com.cleevio.vexl.utils;

import lombok.experimental.UtilityClass;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
public class EncryptionUtils {

    public byte[] decodeBase64String(String value) {
        return Base64.getDecoder().decode(value);
    }

    public PublicKey createPublicKey(String base64PublicKey, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedPublicBytes = Base64.getDecoder().decode(base64PublicKey);
        return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(decodedPublicBytes));
    }

}
