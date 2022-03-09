package com.cleevio.vexl.common.serializer;

import com.cleevio.vexl.utils.EncryptionUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class Base64Deserializer extends StdDeserializer<byte[]> {

    public Base64Deserializer() {
        super(byte[].class);
    }

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return EncryptionUtils.decodeBase64String(p.getText());
    }
}
