package com.gamil.api.tests;
import com.gamil.api.core.*;
import com.gamil.api.clients.*;
import com.gamil.api.data.Cases;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.*;
import java.util.*;
import static com.gamil.api.assertions.ApiChecks.*;
import static org.testng.Assert.*;
public final class OrderApiTest extends BaseApiTest {
 @DataProvider(name="orders") public Object[][] cases() throws Exception { return Cases.load("order-cases.json"); }
 @Test(groups="api",dataProvider="orders") public void createOrderWithDifferentData(JsonNode c) {
  String id=createItem();
  Map<String,Object> line=new LinkedHashMap<>(Map.of("itemId",id,"quantity",2));
  c.path("line").fields().forEachRemaining(e->line.put(e.getKey(),Json.MAPPER.convertValue(e.getValue(),Object.class)));
  for(JsonNode key:c.path("removeLine")) line.remove(key.asText());
  Map<String,Object> body=new LinkedHashMap<>(Map.of("customerId",customerId,"items",List.of(line)));
  c.path("set").fields().forEachRemaining(e->body.put(e.getKey(),Json.MAPPER.convertValue(e.getValue(),Object.class)));
  for(JsonNode key:c.path("remove")) body.remove(key.asText());
  OrderClient client=switch(c.path("auth").asText("admin")) {case "customer"->customerOrders;case "none"->new OrderClient(anonymous);case "invalid"->new OrderClient(invalid);default->orders;};
  int before=orders.list().json().size();
  ApiResult r=rememberOrder(c.has("raw")?client.rawPost("/orders",c.get("raw").asText()):client.create(body));
  status(r,c.path("status").asInt());
  if(r.status()==201) {
   JsonNode n=object(r,201); order(n); int qty=((Number)line.get("quantity")).intValue();
   assertEquals(n.path("customerId").asText(),customerId); assertEquals(n.path("total").asDouble(),12.50*qty,0.001);
   assertEquals(n.path("status").asText(),"pending"); assertFalse(n.path("paid").asBoolean());
   assertEquals(n.path("items").get(0).path("quantity").asInt(),qty);
   assertTrue(contains(customerOrders.list().json(),n.path("id").asText()),"Persisted customer order");
   assertEquals(items.find(id).json().path("stock").asInt(),10-qty,"Stock reduced");
  } else {
   assertEquals(orders.list().json().size(),before,"Rejected request must not create an order");
   assertEquals(items.find(id).json().path("stock").asInt(),10,"Rejected order must not change stock");
   if(r.status()!=401) assertTrue(object(r,r.status()).path("error").isTextual());
  }
 }
}
