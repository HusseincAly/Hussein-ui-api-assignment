package com.gamil.api.auth;
import java.util.Map;
// Strategy: choose authentication without changing endpoint clients.
public interface AuthProvider {
 Map<String,String> headers();
 static AuthProvider bearer(String token) { return () -> Map.of("Authorization", "Bearer " + token); }
 static AuthProvider none() { return Map::of; }
}
