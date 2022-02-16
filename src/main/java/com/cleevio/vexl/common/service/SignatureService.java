package com.cleevio.vexl.common.service;

import com.cleevio.vexl.common.exception.DigitalSignatureException;
import com.cleevio.vexl.utils.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

@Service
@Slf4j
public class SignatureService {

    private static final String EdDSA = "Ed25519";

    //TODO public_key will be in properties
    private static final String publicKey = "MCowBQYDK2VwAyEAUrB4CUnNldgBuC7vuhhCdfuAGzy6YSA5RnkCABa29DE=";

    public boolean isSignatureValid(String publicKey, String phoneHash, String digitalSignature)
            throws DigitalSignatureException, IOException {
        byte[] valueForSign = joinBytes(EncryptionUtils.decodeBase64String(publicKey), EncryptionUtils.decodeBase64String(phoneHash));
        return isSignatureValid(valueForSign, digitalSignature);
    }

    public boolean isSignatureValid(byte[] valueForSign, String digitalSignature)
            throws DigitalSignatureException {
        try {
            Signature signature = Signature.getInstance(EdDSA);
            signature.initVerify(EncryptionUtils.createPublicKey(publicKey, EdDSA));
            signature.update(valueForSign);
            return signature.verify(EncryptionUtils.decodeBase64String(digitalSignature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            String errorMessage = String.format("Error occurred while verifying signature {}, error {}",
                    digitalSignature,
                    e.getMessage());
            log.error(errorMessage);
            throw new DigitalSignatureException(errorMessage, e);
        }
    }

    private byte[] joinBytes(byte[] publicKey, byte[] phoneHash)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(publicKey);
        outputStream.write(phoneHash);

        return outputStream.toByteArray();
    }
}
