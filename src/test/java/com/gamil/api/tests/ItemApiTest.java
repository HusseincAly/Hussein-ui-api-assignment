package com.gamil.api.tests;
import com.gamil.api.core.*;
import com.gamil.api.clients.*;
import com.gamil.api.builders.*;
import com.gamil.api.data.Cases;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.*;
import java.util.*;
import static com.gamil.api.assertions.ApiChecks.*;
import static org.testng.Assert.*;
public final class ItemApiTest extends BaseApiTest {
 @DataProvider(name="items") public Object[][] cases() throws Exception { return Cases.load("item-cases.json"); }
 @Test(groups="api",dataProvider="items") public void createItemWithDifferentData(JsonNode c) {
  Map<String,Object> body=new ItemBuilder().build();
  c.path("set").fields().forEachRemaining(e->body.put(e.getKey(),Json.MAPPER.convertValue(e.getValue(),Object.class)));
  for(JsonNode k:c.path("remove")) body.remove(k.asText());
  ItemClient client=switch(c.path("auth").asText("admin")) { case "customer"->new ItemClient(customer); case "none"->new ItemClient(anonymous); case "invalid"->new ItemClient(invalid); default->items; };
  int before=items.get("/items").json().size();
  ApiResult r=rememberItem(c.has("raw")?client.rawPost("/items",c.get("raw").asText()):client.create(body));
  status(r,c.path("status").asInt());
  if(r.status()==201) {
   JsonNode n=object(r,201); item(n);
   assertEquals(n.path("name").asText(),body.get("name"));
   assertEquals(n.path("price").asDouble(),((Number)body.get("price")).doubleValue(),0.001);
   assertEquals(n.path("stock").asInt(),((Number)body.get("stock")).intValue());
   assertEquals(object(items.find(n.path("id").asText()),200),n,"Persisted item");
  } else {
   assertEquals(items.get("/items").json().size(),before,"Rejected request must not create an item");
   if(r.status()==400||r.status()==403) assertTrue(object(r,r.status()).path("error").isTextual(),"Error message");
  }
 }
}
