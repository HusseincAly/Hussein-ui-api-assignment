package com.gamil.api.assertions;
import com.gamil.api.core.ApiResult;
import com.fasterxml.jackson.databind.JsonNode;
import static org.testng.Assert.*;
public final class ApiChecks {
 public static void status(ApiResult r,int expected) { assertEquals(r.status(),expected,"HTTP status; body: "+r.body()); }
 public static JsonNode object(ApiResult r,int expected) {
  status(r,expected); assertTrue(r.headers().getOrDefault("content-type","").contains("application/json"),"JSON content type");
  JsonNode n=r.json(); assertTrue(n.isObject(),"JSON object"); return n;
 }
 public static void item(JsonNode n) {
  assertTrue(n.path("id").isTextual()); assertFalse(n.path("id").asText().isBlank());
  assertTrue(n.path("name").isTextual()); assertTrue(n.path("price").isNumber()); assertTrue(n.path("stock").isIntegralNumber());
 }
 public static void order(JsonNode n) {
  assertTrue(n.path("id").isTextual()); assertTrue(n.path("customerId").isTextual()); assertTrue(n.path("items").isArray());
  assertTrue(n.path("total").isNumber()); assertTrue(n.path("paid").isBoolean()); assertTrue(n.path("createdAt").isNumber());
 }
 public static boolean contains(JsonNode rows,String id) { for(JsonNode row:rows) if(row.path("id").asText().equals(id)) return true; return false; }
}
