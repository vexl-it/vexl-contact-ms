package com.cleevio.vexl.common.service;

import com.cleevio.vexl.common.config.SecretKeyConfig;
import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.common.service.query.CheckSignatureValidityQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
@RequiredArgsConstructor
public class SignatureService {

    private final SecretKeyConfig secretKey;

    public boolean isSignatureValid(@Valid CheckSignatureValidityQuery validityQuery) {
        String input = String.join("", validityQuery.publicKey(), validityQuery.hash());
        return CLibrary.CRYPTO_LIB.ecdsa_verify(this.secretKey.signaturePublicKey(), input, input.length(), validityQuery.signature());
    }
}
