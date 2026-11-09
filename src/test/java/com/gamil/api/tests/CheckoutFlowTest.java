package com.gamil.api.tests;
import com.gamil.api.builders.ItemBuilder;
import com.gamil.api.workflows.CheckoutWorkflow;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import java.util.*;
import static com.gamil.api.assertions.ApiChecks.*;
import static org.testng.Assert.*;
public final class CheckoutFlowTest extends BaseApiTest {
 @Test(groups="api") public void customerPurchasesProductsAndAdminVerifiesPayment() {
  String a=createItem(); String b=object(rememberItem(items.create(new ItemBuilder().price(20).stock(5).build())),201).path("id").asText();
  JsonNode browsed=new com.gamil.api.clients.ItemClient(customer).get("/items").json();
  assertTrue(contains(browsed,a));assertTrue(contains(browsed,b));
  CheckoutWorkflow.Purchase flow=new CheckoutWorkflow(orders,customerOrders).purchase(customerId,List.of(Map.of("itemId",a,"quantity",2),Map.of("itemId",b,"quantity",1)),orderIds::add);
  JsonNode pending=object(flow.created(),201),paid=object(flow.paid(),200);order(pending);order(paid);
  assertEquals(pending.path("status").asText(),"pending");assertFalse(pending.path("paid").asBoolean());
  assertEquals(paid.path("id"),pending.path("id"));assertEquals(paid.path("customerId").asText(),customerId);
  assertEquals(paid.path("total").asDouble(),45.0,0.001);assertTrue(paid.path("paid").asBoolean());assertEquals(paid.path("status").asText(),"paid");
  String id=paid.path("id").asText();
  assertTrue(contains(customerOrders.list().json(),id));assertTrue(contains(orders.paid().json(),id));
  assertEquals(items.find(a).json().path("stock").asInt(),8);assertEquals(items.find(b).json().path("stock").asInt(),4);
  status(customerOrders.checkout(id),400); // Duplicate checkout must be rejected.
  status(orders.remove(id),204);orderIds.remove(id);
  assertFalse(contains(customerOrders.list().json(),id));
  assertEquals(items.find(a).json().path("stock").asInt(),10);assertEquals(items.find(b).json().path("stock").asInt(),5);
 }
}
