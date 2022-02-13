package com.cleevio.vexl.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ToLowerCaseDeserializer extends StdDeserializer<String> {

	public ToLowerCaseDeserializer() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return _parseString(p, ctxt).toLowerCase();
	}

}
