package com.gamil.api.core;
import com.microsoft.playwright.APIResponse;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
// Adapter: retain response facts, then release Playwright's response buffer.
public record ApiResult(int status, Map<String,String> headers, String body) {
 public static ApiResult read(APIResponse r) { try { return new ApiResult(r.status(),r.headers(),r.text()); } finally { r.dispose(); } }
 public JsonNode json() { return Json.read(body); }
}
