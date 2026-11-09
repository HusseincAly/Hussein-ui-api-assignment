package com.gamil.api.clients;
import com.gamil.api.core.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import java.nio.charset.StandardCharsets;
public class ApiClient {
 protected final APIRequestContext http;
 public ApiClient(APIRequestContext http) { this.http=http; }
 public ApiResult get(String path) { return ApiResult.read(http.get(path)); }
 public ApiResult post(String path,Object body) { return rawPost(path,Json.write(body)); }
 public ApiResult rawPost(String path,String body) { return ApiResult.read(http.post(path,RequestOptions.create().setHeader("Content-Type","application/json").setData(body.getBytes(StandardCharsets.UTF_8)))); }
 public ApiResult delete(String path) { return ApiResult.read(http.delete(path)); }
}
