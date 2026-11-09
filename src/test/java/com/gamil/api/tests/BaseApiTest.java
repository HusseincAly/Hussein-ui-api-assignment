package com.gamil.api.tests;
import com.gamil.config.Config;
import com.gamil.api.auth.*;
import com.gamil.api.core.*;
import com.gamil.api.clients.*;
import com.microsoft.playwright.*;
import org.testng.annotations.*;
import java.util.*;
import static com.gamil.api.assertions.ApiChecks.*;
public class BaseApiTest {
 protected Playwright pw; protected APIRequestContext admin,customer,anonymous,invalid;
 protected ItemClient items; protected OrderClient orders,customerOrders;
 protected String customerId;
 protected final List<String> itemIds=new ArrayList<>(),orderIds=new ArrayList<>();
 @BeforeClass(alwaysRun=true) public void setupApi() {
  pw=Playwright.create(new Playwright.CreateOptions().setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD","1")));
  String url=Config.get("API_BASE_URL","http://localhost:3000");
  anonymous=ApiContextFactory.create(pw,url,AuthProvider.none());
  admin=ApiContextFactory.create(pw,url,AuthProvider.bearer(login("CAPSTONE_ADMIN")));
  customer=ApiContextFactory.create(pw,url,AuthProvider.bearer(login("CAPSTONE_CUSTOMER")));
  invalid=ApiContextFactory.create(pw,url,AuthProvider.bearer("invalidtoken"));
  customerId=Config.required("CAPSTONE_CUSTOMER_ID");
  items=new ItemClient(admin); orders=new OrderClient(admin); customerOrders=new OrderClient(customer);
 }
 private String login(String prefix) {
  ApiResult r=new ApiClient(anonymous).post("/login",Map.of("username",Config.required(prefix+"_USERNAME"),"password",Config.required(prefix+"_PASSWORD")));
  // Do not include the successful token response in diagnostic output.
  if(r.status()!=200) throw new AssertionError(prefix+" login failed: HTTP "+r.status());
  String token=r.json().path("token").asText(); if(token.isBlank()) throw new AssertionError("Missing login token"); return token;
 }
 protected ApiResult rememberItem(ApiResult r) { if(r.status()==201) itemIds.add(r.json().path("id").asText()); return r; }
 protected ApiResult rememberOrder(ApiResult r) { if(r.status()==201) orderIds.add(r.json().path("id").asText()); return r; }
 protected String createItem() { return object(rememberItem(items.create(new com.gamil.api.builders.ItemBuilder().build())),201).path("id").asText(); }
 @AfterMethod(alwaysRun=true) public void cleanup() {
  List<String> errors=new ArrayList<>();
  for(String id:orderIds) try { status(orders.remove(id),204); } catch(Throwable t) { errors.add("Order "+id+": "+t.getMessage()); }
  orderIds.clear();
  for(String id:itemIds) try { status(items.remove(id),204); } catch(Throwable t) { errors.add("Item "+id+": "+t.getMessage()); }
  itemIds.clear(); if(!errors.isEmpty()) throw new AssertionError(String.join("; ",errors));
 }
 @AfterClass(alwaysRun=true) public void closeApi() {
  try { for(APIRequestContext c:new APIRequestContext[]{admin,customer,anonymous,invalid}) if(c!=null) c.dispose(); } finally { if(pw!=null) pw.close(); }
 }
}
