package com.gamil.api.core;
import com.gamil.api.auth.AuthProvider;
import com.microsoft.playwright.*;
import java.util.*;
// Factory: shared request setup follows Ahmed's API session factory.
public final class ApiContextFactory {
 public static APIRequestContext create(Playwright pw,String url,AuthProvider auth) {
  Map<String,String> headers=new HashMap<>(auth.headers()); headers.put("Accept","application/json");
  return pw.request().newContext(new APIRequest.NewContextOptions().setBaseURL(url).setExtraHTTPHeaders(headers).setTimeout(30000));
 }
}
