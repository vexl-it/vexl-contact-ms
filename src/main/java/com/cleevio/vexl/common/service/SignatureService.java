package com.cleevio.vexl.common.service;

import com.cleevio.vexl.common.config.SecretKeyConfig;
import com.cleevio.vexl.common.cryptolib.CLibrary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SecretKeyConfig secretKey;

    public boolean isSignatureValid(String publicKey, String hash, String signature) {
        String input = String.join("", publicKey, hash);
        return CLibrary.CRYPTO_LIB.ecdsa_verify(this.secretKey.signaturePublicKey(), input, input.length(), signature);
    }
}
