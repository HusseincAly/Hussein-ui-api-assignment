package com.gamil.ui.tests;
import com.gamil.config.Config;
import com.gamil.ui.pages.*;
import com.microsoft.playwright.*;
import org.testng.annotations.*;
import org.testng.ITestResult;
import java.nio.file.*;
import java.util.*;
public final class OrangeHrmTest {
 private Playwright pw;private Browser browser;private BrowserContext context;private Page page;
 private AdminPage admin;private boolean created;private String username;
 @BeforeClass(alwaysRun=true) public void startBrowser() {
  pw=Playwright.create(new Playwright.CreateOptions().setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD","1")));
  BrowserType.LaunchOptions options=new BrowserType.LaunchOptions().setHeadless(Boolean.parseBoolean(Config.get("HEADLESS","true")));
  String channel=Config.get("BROWSER_CHANNEL","");if(!channel.isBlank()) options.setChannel(channel);
  browser=pw.chromium().launch(options);context=browser.newContext(new Browser.NewContextOptions().setViewportSize(1440,1000));
  context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
  page=context.newPage();page.setDefaultTimeout(30000);
 }
 @Test(groups="ui") public void adminAddsAndSearchesForUser() throws Exception {
  username="Hussein"+UUID.randomUUID().toString().replace("-","").substring(0,10);
  page.navigate(Config.get("UI_BASE_URL","https://opensource-demo.orangehrmlive.com/"));
  new LoginPage(page).login(Config.get("ORANGEHRM_USERNAME","Admin"),Config.get("ORANGEHRM_PASSWORD","admin123"));
  admin=new AdminPage(page);admin.open();
  String employee=admin.addUser().save(username,"Train!9"+UUID.randomUUID().toString().substring(0,12),Config.get("ORANGEHRM_EMPLOYEE_HINT","a"));
  created=true;admin.search(username);admin.verifyUser(username,"Admin",employee,"Enabled");
  Files.createDirectories(Path.of("target/evidence"));
  page.screenshot(new Page.ScreenshotOptions().setPath(Path.of("target/evidence/orangehrm-search.png")).setFullPage(true));
 }
 @AfterMethod(alwaysRun=true) public void captureAndCleanup(ITestResult result) throws Exception {
  if(page==null) return;
  if(!result.isSuccess()) { Files.createDirectories(Path.of("target/evidence"));page.screenshot(new Page.ScreenshotOptions().setPath(Path.of("target/evidence/orangehrm-failure.png"))); }
  if(admin!=null && username!=null) { admin.open();admin.deleteCreatedUser(username);created=false; }
 }
 @AfterClass(alwaysRun=true) public void closeBrowser() throws Exception {
  try { if(context!=null) { Files.createDirectories(Path.of("target/evidence"));context.tracing().stop(new Tracing.StopOptions().setPath(Path.of("target/evidence/orangehrm-trace.zip")));context.close(); } }
  finally { try { if(browser!=null) browser.close(); } finally {if(pw!=null) pw.close();} }
 }
}
