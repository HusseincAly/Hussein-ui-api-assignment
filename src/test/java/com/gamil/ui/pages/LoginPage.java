package com.gamil.ui.pages;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
public final class LoginPage {
 private final Page page;
 public LoginPage(Page page) { this.page=page; }
 public void login(String username,String password) {
  page.getByPlaceholder("Username",new Page.GetByPlaceholderOptions().setExact(true)).fill(username);
  page.getByPlaceholder("Password",new Page.GetByPlaceholderOptions().setExact(true)).fill(password);
  page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Login").setExact(true)).click();
  page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Admin").setExact(true)).waitFor();
 }
}
