package com.gamil.api.clients;
import com.gamil.api.core.ApiResult;
import com.microsoft.playwright.APIRequestContext;
public final class ItemClient extends ApiClient {
 public ItemClient(APIRequestContext http) { super(http); }
 public ApiResult create(Object body) { return post("/items",body); }
 public ApiResult find(String id) { return get("/items/"+id); }
 public ApiResult remove(String id) { return delete("/items/"+id); }
}
