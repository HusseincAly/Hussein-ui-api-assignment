package com.gamil.api.clients;
import com.gamil.api.core.ApiResult;
import com.microsoft.playwright.APIRequestContext;
public final class OrderClient extends ApiClient {
 public OrderClient(APIRequestContext http) { super(http); }
 public ApiResult create(Object body) { return post("/orders",body); }
 public ApiResult list() { return get("/orders"); }
 public ApiResult paid() { return get("/orders/paid"); }
 public ApiResult checkout(String id) { return post("/orders/"+id+"/checkout",java.util.Map.of()); }
 public ApiResult remove(String id) { return delete("/orders/"+id); }
}
