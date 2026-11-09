package com.gamil.ui.pages;
import com.microsoft.playwright.*;
import com.gamil.api.core.Json;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
public final class AdminPage {
 private final Page page;
 public AdminPage(Page page) { this.page=page; }
 public void open() { page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Admin").setExact(true)).click(); page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Search").setExact(true)).waitFor(); }
 public AddUserPage addUser() { page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Add")).click(); return new AddUserPage(page); }
 public void search(String name) {
  page.locator(".oxd-input-group").filter(new Locator.FilterOptions().setHas(page.getByText("Username",new Page.GetByTextOptions().setExact(true)))).locator("input").fill(name);
  Response response=page.waitForResponse(r->r.url().contains("/api/v2/admin/users") && r.url().contains("username="+name) && r.request().method().equals("GET"),()->page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Search").setExact(true)).click());
  if(response.status()!=200) throw new AssertionError("User search failed: "+response.status());
  var results=Json.read(response.text()).path("data");
  if(!results.isArray()) throw new AssertionError("User search did not return a result list");
  assertThat(page.locator(".oxd-table-card")).hasCount(results.size());
 }
 private Locator userRow(String name) { return page.locator(".oxd-table-card").filter(new Locator.FilterOptions().setHas(page.getByText(name,new Page.GetByTextOptions().setExact(true)))); }
 public void verifyUser(String name,String role,String employee,String status) {
  assertThat(page.locator(".oxd-table-card")).hasCount(1);
  Locator row=userRow(name); assertThat(row).hasCount(1);
  assertThat(row.getByText(role,new Locator.GetByTextOptions().setExact(true))).isVisible();
  assertThat(row.getByText(employee,new Locator.GetByTextOptions().setExact(true))).isVisible();
  assertThat(row.getByText(status,new Locator.GetByTextOptions().setExact(true))).isVisible();
 }
 public void deleteCreatedUser(String name) {
  search(name); Locator row=userRow(name); if(row.count()==0) return; assertThat(row).hasCount(1);
  row.locator("button").filter(new Locator.FilterOptions().setHas(page.locator(".bi-trash"))).click();
  page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Yes, Delete")).click();
  assertThat(page.getByText("Successfully Deleted",new Page.GetByTextOptions().setExact(true))).isVisible();
  search(name); assertThat(userRow(name)).hasCount(0);
 }
}
