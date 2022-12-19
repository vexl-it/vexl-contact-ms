package com.cleevio.vexl.common.service;

import com.cleevio.vexl.common.config.SecretKeyConfig;
import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.common.service.query.CheckSignatureValidityQuery;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
@RequiredArgsConstructor
public class SignatureService {

    private final SecretKeyConfig secretKey;

    public boolean isSignatureValid(@Valid CheckSignatureValidityQuery validityQuery, final int cryptoVersion) {
        final String input = String.join(StringUtils.EMPTY, validityQuery.publicKey(), validityQuery.hash());
        if(cryptoVersion >= 2) {
            return CLibrary.CRYPTO_LIB.ecdsa_verify_v2(this.secretKey.signaturePublicKey(), input, input.length(), validityQuery.signature());
        }
        return CLibrary.CRYPTO_LIB.ecdsa_verify(this.secretKey.signaturePublicKey(), input, input.length(), validityQuery.signature());
    }
}
