package com.gamil.api.core;
import com.fasterxml.jackson.databind.*;
public final class Json {
 public static final ObjectMapper MAPPER = new ObjectMapper();
 private Json() {}
 public static JsonNode read(String text) { try { return MAPPER.readTree(text); } catch(Exception e) { throw new IllegalArgumentException("Invalid JSON response",e); } }
 public static String write(Object value) { try { return MAPPER.writeValueAsString(value); } catch(Exception e) { throw new IllegalArgumentException(e); } }
}
