package com.gamil.config;
public final class Config {
 private Config() {}
 public static String get(String key, String fallback) { return System.getenv().getOrDefault(key, fallback); }
 public static String required(String key) {
  String value = System.getenv(key);
  if (value == null || value.isBlank()) throw new IllegalStateException("Set environment variable " + key + " (see README.md)");
  return value;
 }
}
