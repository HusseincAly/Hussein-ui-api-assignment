package com.gamil.ui.pages;
import com.microsoft.playwright.*;
import com.gamil.api.core.Json;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
public final class AddUserPage {
 private final Page page;
 public AddUserPage(Page page) { this.page=page; }
 private Locator group(String label) { return page.locator(".oxd-input-group").filter(new Locator.FilterOptions().setHas(page.getByText(label,new Page.GetByTextOptions().setExact(true)))); }
 private void select(String label,String value) {
  group(label).locator(".oxd-select-text").click();
  page.getByRole(AriaRole.OPTION,new Page.GetByRoleOptions().setName(value).setExact(true)).click();
 }
 public String save(String username,String password,String employeeHint) {
  select("User Role","Admin");select("Status","Enabled");
  Response employees=page.waitForResponse(r->r.url().contains("/api/v2/pim/employees?") && r.request().method().equals("GET"),()->group("Employee Name").locator("input").fill(employeeHint));
  var candidates=Json.read(employees.text()).path("data");
  if(!candidates.isArray() || candidates.isEmpty()) throw new AssertionError("No matching employee found");
  Locator suggestions=page.getByRole(AriaRole.OPTION).filter(new Locator.FilterOptions().setHasNotText("Searching")).filter(new Locator.FilterOptions().setHasNotText("No Records Found"));
  suggestions.first().waitFor();String selected=suggestions.first().innerText().trim();
  String employee=null;
  for(var candidate:candidates) {
   String first=candidate.path("firstName").asText();String middle=candidate.path("middleName").asText("");String last=candidate.path("lastName").asText();
   String full=(first+" "+middle+" "+last).trim().replaceAll("\\s+"," ");
   if(full.equals(selected.replaceAll("\\s+"," "))) { employee=(first+" "+last).trim();break; }
  }
  if(employee==null) throw new AssertionError("Selected employee did not match autocomplete data");
  suggestions.first().click();
  group("Username").locator("input").fill(username);
  group("Password").locator("input").fill(password);group("Confirm Password").locator("input").fill(password);
  page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Save").setExact(true)).click();
  assertThat(page.getByText("Successfully Saved",new Page.GetByTextOptions().setExact(true))).isVisible();
  page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Search").setExact(true)).waitFor();
  return employee;
 }
}
