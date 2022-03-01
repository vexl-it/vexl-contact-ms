package com.cleevio.vexl.common.enums;

public enum AlgorithmEnum {

    EdDSA("Ed25519"),
    SHA256("SHA-256"),
    ECIES("EC"),
    ECDSA("SHA1WithECDSA"),
    HMACSHA256("HmacSHA256");

    private final String value;

    AlgorithmEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
