package com.gamil.api.workflows;
import com.gamil.api.clients.OrderClient;
import com.gamil.api.core.ApiResult;
import java.util.*;
import java.util.function.Consumer;
// Facade: encapsulates the documented admin-create/customer-checkout sequence.
public final class CheckoutWorkflow {
 private final OrderClient admin,customer;
 public CheckoutWorkflow(OrderClient admin,OrderClient customer) { this.admin=admin;this.customer=customer; }
 public record Purchase(ApiResult created,ApiResult paid) {}
 public Purchase purchase(String customerId,List<Map<String,Object>> lines,Consumer<String> track) {
  ApiResult created=admin.create(Map.of("customerId",customerId,"items",lines));
  if(created.status()!=201) throw new AssertionError("Create order failed: "+created.body());
  String id=created.json().path("id").asText(); track.accept(id);
  return new Purchase(created,customer.checkout(id));
 }
}
