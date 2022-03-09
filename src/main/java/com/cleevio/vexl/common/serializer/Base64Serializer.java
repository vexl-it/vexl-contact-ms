package com.cleevio.vexl.common.serializer;

import com.cleevio.vexl.utils.EncryptionUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class Base64Serializer extends StdSerializer<byte[]> {

    public Base64Serializer() {
        super(byte[].class);
    }

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(EncryptionUtils.encodeToBase64String(value));
    }
}
