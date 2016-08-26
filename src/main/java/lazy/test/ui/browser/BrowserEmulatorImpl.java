package lazy.test.ui.browser;

//import com.thoughtworks.selenium.Wait;

import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * BrowserEmulator is based on Selenium2 and adds some enhancements
 */
public class BrowserEmulatorImpl implements BrowserEmulator {

    RemoteWebDriver browserCore;
    WebDriverBackedSelenium browser;
    ChromeDriverService chromeServer;
    JavascriptExecutor javaScriptExecutor;
    WebDriverWait webDriverWait;

    String chromeDriverPath;
    String ieDriverPath;

    String AndroidDeviceSerial = GlobalSettings.AndroidDeviceSerial;
    String EmulatorDeviceName = GlobalSettings.EmulatorDeviceName;

    int stepInterval = Integer.parseInt(GlobalSettings.stepInterval);
    int timeout = Integer.parseInt(GlobalSettings.timeout);
    int pause = Integer.parseInt(GlobalSettings.pause);

    private static Logger logger = LoggerFactory.getLogger(BrowserEmulatorImpl.class);

    public BrowserEmulatorImpl() {
        chromeDriverPath = GlobalSettings.chromeDriverPath;
        ieDriverPath = GlobalSettings.ieDriverPath;

        logger.info("chromeDriverPath:{}", chromeDriverPath);
        logger.info("ieDriverPath:{}", ieDriverPath);

        if (!chromeDriverPath.startsWith("/")) {
            chromeDriverPath = "/" + chromeDriverPath;
        }

        if (!ieDriverPath.startsWith("/")) {
            ieDriverPath = "/" + ieDriverPath;
        }

        chromeDriverPath = this.getClass().getResource(chromeDriverPath).getPath();
        ieDriverPath = this.getClass().getResource(ieDriverPath).getPath();

        setupBrowserCoreType(GlobalSettings.browserCoreType);
//        browser = new WebDriverBackedSelenium(browserCore, "http://www.163.com/");
        javaScriptExecutor = (JavascriptExecutor) browserCore;
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        logger.info("Started BrowserEmulator");
    }

    private void setupBrowserCoreType(int type) {
        if (type == 1) {
            browserCore = new FirefoxDriver();
            browserCore.manage().window().maximize();
            logger.info("Using Firefox");
            return;
        }
        if (type == 2) {
            chromeServer = new ChromeDriverService.Builder().usingDriverExecutable(new File(chromeDriverPath)).usingAnyFreePort().build();
            try {
                chromeServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
            browserCore = new RemoteWebDriver(chromeServer.getUrl(), capabilities);
            browserCore.manage().window().maximize();
            logger.info("Using Chrome");
            return;
        }
        if (type == 3) {
            System.setProperty("webdriver.ie.driver", ieDriverPath);
            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            browserCore = new InternetExplorerDriver(capabilities);
            browserCore.manage().window().maximize();
            logger.info("Using IE");
            return;
        }
        if (type == 4) {
            browserCore = new SafariDriver();
            browserCore.manage().window().maximize();
            logger.info("Using Safari");
            return;
        }
        if(type == 5){
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("androidPackage", "com.android.chrome");
            chromeOptions.setExperimentalOption("androidDeviceSerial", AndroidDeviceSerial);
            browserCore = new ChromeDriver(new ChromeDriverService.Builder().usingDriverExecutable(new File(chromeDriverPath)).usingAnyFreePort().build(),chromeOptions);
            logger.info("Using Chrome Browser on Android Device,Serial is: {}", AndroidDeviceSerial);
            return;
    }
    if(type == 6){
        Map<String, String> mobileEmulation = new HashMap<String, String>();
        mobileEmulation.put("deviceName", EmulatorDeviceName);
        Map<String, Object> Options = new HashMap<String, Object>();
        Options.put("mobileEmulation", mobileEmulation);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, Options);

        browserCore = new ChromeDriver(new ChromeDriverService.Builder().usingDriverExecutable(new File(chromeDriverPath)).usingAnyFreePort().build(),capabilities);
        logger.info("Using H5-Emulator on PC Chrome ");
        return;
    }
        Assert.fail("Incorrect browser type");
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getBrowserCore()
	 */
    @Override
	public RemoteWebDriver getBrowserCore() {
        return browserCore;
    }

    @Override
    public WebDriverWait getWebDriverWait() {
        return webDriverWait;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getBrowser()
	 */
//    @Override
//	public WebDriverBackedSelenium getBrowser() {
//        return browser;
//    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getJavaScriptExecutor()
	 */
    @Override
	public JavascriptExecutor getJavaScriptExecutor() {
        return javaScriptExecutor;
    }
    /**
	 * 返回当前聚焦的window的title
	 */
    public String getCurrentWindowTitle(){
    	return browserCore.getTitle();
    }
    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#open(java.lang.String)
	 */
    @Override
	public void open(String url) {
//        pause(pause);
        try {
            browserCore.get(url);
        } catch (Exception e) {
            e.printStackTrace();
            handleFailure("Failed to open url " + url);
        }
        pause(pause);
        logger.info("Opened url " + url);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#quit()
	 */
    @Override
	public void quit() {
        //替换
        pause(pause);
        browserCore.quit();
        int type = GlobalSettings.browserCoreType;
        if ( type== 2 || type == 5 || type == 6) {
            chromeServer.stop();
        }
        logger.info("Success to quit BrowserEmulator");
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#click(java.lang.String)
	 */
    @Override
	public void click(String xpath) {
        click(new String[] {xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#click(java.lang.String[])
	 */
    @Override
	public void click(String[] xpath) {
        pause(pause);
        try {
            clickTheClickable(xpath);
            logger.info("Succeed to click[" + StringUtils.join(xpath, ",") + "]");
        } catch (Exception e) {
            logger.error("Failed to click element by xpathArray:{} , exception:{}" , StringUtils.join(xpath, ", "), e.getMessage());
            handleFailure("Click failed");
        }

    }

    /**
     * Click an element until it's clickable or timeout
     * @param xpathArray
     * @throws Exception
     */
    private void clickTheClickable(final String[] xpathArray) throws Exception {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath: xpathArray) {
                        try {
                            WebElement e = findElement(xpath);
                            if (null != e && e.isDisplayed() && e.isEnabled()) {
                                e.click();
                                flag = true;
                                break;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to clickTheClickable element by:{}", StringUtils.join(xpathArray,","));
        } catch (Exception e) {
            logger.error("Failed to clickTheClickable element by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure("Click failed");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#check(java.lang.String)
	 */
    @Override
	public void check(String xpath) {
        check(new String[] {xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#check(java.lang.String[])
	 */
    @Override
	public void check(final String[] xpathArray) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();
                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().checked=true;");
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to check element by:{}", StringUtils.join(xpathArray,","));
        }catch (Exception e){
            logger.error("Failed to check element by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure("Check failed");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isChecked(java.lang.String)
	 */
    @Override
	public boolean isChecked(String xpath) {
        WebElement element = findElement(xpath);
        return Boolean.parseBoolean(element.getAttribute("checked"));
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#type(java.lang.String, java.lang.String)
	 */
    @Override
	public void type(String xpath, String text) {
        type(new String[]{xpath},text);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#type(java.lang.String[], java.lang.String)
	 */
    @Override
	public void type(final String[] xpathArray, final String text) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                boolean flag = false;
                @Override
                public Boolean apply(WebDriver driver) {
                    for (String xpath : xpathArray) {
                        try {
                            WebElement we = findElement(xpath);
                            we.clear();
                            we.sendKeys(text);
                            logger.info("Type " + text + " at " + xpath);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }

                    return flag;
                }
            });
            logger.info("Succeed to Type element by:{}", StringUtils.join(xpathArray,","));
        }catch (Exception e){
            logger.error("Failed to type by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure("Type failed");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#clear(java.lang.String)
	 */
    @Override
	public void clear(String xpath) {
        clear(new String[] {xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#clear(java.lang.String[])
	 */
    @Override
	public void clear(final String[] xpathArray) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                boolean flag = false;
                @Override
                public Boolean apply(WebDriver driver) {
                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();
                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().value='';");
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to Clear element by:{}", StringUtils.join(xpathArray,","));
        } catch (Exception e) {
            logger.error("Failed to clear by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure("Clear failed");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#uncheck(java.lang.String)
	 */
    @Override
	public void uncheck(String xpath) {
        uncheck(new String[] {xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#uncheck(java.lang.String[])
	 */
    @Override
	public void uncheck(final String[] xpathArray) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                boolean flag = false;
                @Override
                public Boolean apply(WebDriver driver) {
                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();
                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().checked=false;");
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to uncheck element by:{}", StringUtils.join(xpathArray,","));
        } catch (Exception e) {
            logger.error("Failed to uncheck by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure("Unchedk failed");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#calendarInput(java.lang.String, java.lang.String)
	 */
    @Override
	public void calendarInput(String xpath, String date) {
        calendarInput(new String[] {xpath}, date);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#calendarInput(java.lang.String[], java.lang.String)
	 */
    @Override
	public void calendarInput(final String[] xpathArray, final String date) {
        //检查date是否是正确的日期格式
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.parse(date);
        }catch(Exception e){
            logger.warn("The input date formate is wrong: " + date);
            boolean dateFormat = false;
            Assert.assertTrue(dateFormat,"The input date formate:" + date + " is wrong!");
        }
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);

        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();
                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().value='" + date + "';");
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to input calendar by:{}", StringUtils.join(xpathArray,","));
        } catch (Exception e) {
            logger.error("Failed to input calendar by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#mouseOver(java.lang.String)
	 */
    @Override
	public void mouseOver(String xpath) {
        mouseOver(new String[] {xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#mouseOver(java.lang.String[])
	 */
    @Override
	public void mouseOver(final String[] xpathArray) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);

        final int browserType = GlobalSettings.browserCoreType;
        // Selenium doesn't support the Safari browser
        if (browserType == 4 || browserType == 5 || browserType == 6) {
            Assert.fail("Mouseover is not supported for this browser now");
            Assert.fail("Incorrect browser type");
        }

        //expectElementExistOrNot(true, xpath, timeout);
        // First make mouse out of browser
        Robot rb = null;
        try {
            rb = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        rb.mouseMove(0, 0);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    Boolean flag = false;
                    for (String xpath : xpathArray) {
                        WebElement we = findElement(xpath);
                        //5 和 6 因项目没有该事件，没有测试完成
                        if (browserType == 2) {
                            try {
                                Actions builder = new Actions(browserCore);
                                builder.moveToElement(we).build().perform();
                                flag = true;
                                break;
                            } catch (Exception e) {
                                logger.error("Failed to mouseover " + xpath, e.getMessage());
                                flag = false;
                            }

                            logger.info("Mouseover " + xpath);
                        } else if (browserType == 1 || browserType == 3) {
                            try {
                                for (int i = 0; i < 5; i++) {//编译器警告，是否有必要
                                    Actions builder = new Actions(browserCore);
                                    builder.moveToElement(we).build().perform();
                                    logger.info("Mouseover " + xpath);
                                    flag = true;
                                    break;
                                }
                            }catch (Exception e) {
                                logger.error("Failed to mouseover " + xpath, e.getMessage());
                                flag = false;
                            }
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to mouseOver by xpathArray:{} ", StringUtils.join(xpathArray, ", "));
        }catch (Exception e){
            logger.error("Failed to mouseOver by xpathArray:{} , exception:{}" , StringUtils.join(xpathArray, ", "), e.getMessage());
            handleFailure("Failed to mouseOver");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#selectWindow(java.lang.String)
	 */
    @Override
	public void selectWindow(String windowTitle) {
        browserCore.switchTo().window(windowTitle);
        logger.info("Switched to window " + windowTitle);
    }
    /**
	 * Switch window/tab
	 * 根据title中包含的字符串来切换浏览器的选项卡
	 * 如果有重名的，默认切换到找到的第一个
	 * 不包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param windowTitleWord
	 *            the window/tab's title
	 * @throws Exception 
	 */
	public void selectWindowFuzzy(String windowTitleWord) {
		boolean findit = false;
		try {
			selectWindow(windowTitleWord);
		} catch (Exception e) {
            Set<String> handles = browserCore.getWindowHandles();
            Iterator<String> it = handles.iterator();
            while(it.hasNext()){
                if(windowTitleWord== it.next()) {
                    browserCore.switchTo().window(it.next());
                    logger.info("Switched to window " + browserCore.getTitle());
                    findit = true;
                    break;
                }
			}
			if(findit==false){
				throw new RuntimeException("Switched to window fail !!!!");
			}
		}
        logger.info("Succeed to select window by:{}", windowTitleWord);
	}
	
	/**
	 * Switch window/tab
	 * 切换到driver打开的唯一的window，通常关掉其他窗口后，需要切换回原窗口（driver只剩原窗口）时使用
	 * @throws Exception 
	 */
	public void selectTheOnlyWindow(){
		Set<String> winHandle = browserCore.getWindowHandles();
		if(winHandle.size()==1){
			browserCore.switchTo().window(winHandle.iterator().next());
		}else{
			throw new RuntimeException("driver已经没有打开的窗口了");
		}
        logger.info("Succeed to select the only window ");
	}
	
    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#enterFrame(java.lang.String)
	 */
    @Override
	public void enterFrame(String xpath) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(xpath)));
            logger.info("Enter frame ");
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
        logger.info("Succeed to enter frame by xpath:{}", xpath);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#enterFrameByNameOrId(java.lang.String)
	 */
    @Override
	public void enterFrameByNameOrId(final String nameOrId) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
            logger.info("Succeed to enter frame by {}", nameOrId);
        } catch (Exception e) {
            logger.error("Failed to enter frame by {}, exception:{}", nameOrId, e.getMessage());
            handleFailure("Failed to enter frame");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#leaveFrame()
	 */
    @Override
	public void leaveFrame() {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try{
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag;
                    try {
                        browserCore.switchTo().defaultContent();
                        flag = true;

                    }catch (Exception e) {
                        flag = false;
                    }
                    return  flag;
                }
            });
            logger.info("Succeed to leave the frame");
        }catch (Exception e){
            logger.error("Failed to leave frame, exception:{}", e.getMessage());
            handleFailure("Failed to leave frame");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#refresh()
	 */
    @Override
	public void refresh() {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>() {
                Boolean flag = false;
                @Override
                public Boolean apply(WebDriver driver) {
                    String orgTitle = driver.getTitle();
                    driver.navigate().refresh();
                    if (orgTitle.equals(driver.getTitle())) {
                        logger.info("Refreshed");
                        flag = true;
                    } else {
                        flag = false;
                    }
                    return flag;
                }
            }));
            logger.info("Succeed to refresh page");
        }catch (Exception e){
            logger.error("Failed to refresh page , exception:{}", e.getMessage());
            handleFailure("Failed to refresh");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#pressKeyboard(int)
	 */
    @Override
	public void pressKeyboard(final int keyCode) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag;
                    try {
                        Robot rb = new Robot();
                        rb.keyPress(keyCode);    // press key
                        rb.delay(100);            // delay 100ms
                        rb.keyRelease(keyCode);    // release key
                        logger.info("Pressed key with code " + keyCode);
                        flag = true;
                    } catch (Exception e) {
                        flag = false;
                    }

                    return flag;
                }
            });
            logger.info("Succeed to pressKeyboard by {}", keyCode);
        } catch (Exception e) {
            logger.error("Failed to pressKeyboard by {}, exception:{}", keyCode, e.getMessage());
            handleFailure("Failed to pressKeyboard");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#inputKeyboard(java.lang.String)
	 */
    @Override
	public void inputKeyboard(final String text) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag;
                    Process p = null;
                    try {
                        String cmd = System.getProperty("user.dir") + "\\res\\SeleniumCommand.exe" + " sendKeys " + text;
                        p = Runtime.getRuntime().exec(cmd);
                        p.waitFor();
                        logger.info("Pressed key with string " + text);
                        flag = true;
                    } catch (Exception e) {
                        logger.info("Failed to press key string " + text);
                        flag = false;
                    } finally {
                        if (p != null) p.destroy();
                    }
                    return flag;
                }
            });
            logger.info("Succeed to inputKeyboard by {}", text);
        } catch (Exception e) {
            logger.error("Failed to inputKeyboard by {}, exception:{}", text, e.getMessage());
            handleFailure("Failed to inputKeyboard");
        }
    }

    //TODO Mimic system-level mouse event

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isTextExists(java.lang.String)
	 */
    @Override
	public boolean isTextExists(String text) {
        return isTextExists(new String[] {text});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isTextExists(java.lang.String[])
	 */
    @Override
	public boolean isTextExists(final String[] textArray) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            Boolean res = webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (int i = 0; i < textArray.length; i++) {
                        flag = isTextPresent(textArray[i]);
                        if (flag) {
                            break;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to find text by:{}", StringUtils.join(textArray, ","));
            return res;
        }catch(Exception e){
            logger.error("Failed to find text by:{}, exception:{}", StringUtils.join(textArray, ","), e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#expectTextExistOrNot(boolean, java.lang.String)
	 */
    @Override
	public void expectTextExistOrNot(boolean expectExist, String text) {
        expectTextExistOrNot(expectExist, text, timeout);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#expectTextExistOrNot(boolean, java.lang.String, int)
	 */
    @Override
	public void expectTextExistOrNot(final boolean expectExist, final String text, int timeout) {
        webDriverWait = new WebDriverWait(browserCore, Long.valueOf(timeout)/1000);
        Boolean res = false;
        try {
            res = webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return browserCore.getPageSource().contains(text);
                }
            });
        }catch (Exception e){
            res = false;
        }
        if(expectExist){
            if(res){
                logger.info("Expect desired text:{} and found it ", text);
            } else {
                logger.error("Expect desired text:{} ,but not found it ", text);
                handleFailure("Expect desired text:" + text + " ,but not found it ");
            }
        }else{
            if(!res){
                logger.info("Expect undesired text:{} and not found it ", text);
            } else {
                logger.error("Expect undesired text:{},but found it ", text);
                handleFailure("Expect undesired text:" + text + ",but found it ");
            }
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#expectElementExistOrNot(boolean, java.lang.String, int)
	 */
    @Override
	public void expectElementExistOrNot(boolean expectExist, String xpath, int timeout) {
        expectElementExistOrNot(expectExist, new String[] {xpath}, timeout);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#expectElementExistOrNot(boolean, java.lang.String[], int)
	 */
    @Override
	public void expectElementExistOrNot(boolean expectExist, final String[] xpathArray, int timeout) {
        boolean flag = false;
        webDriverWait = new WebDriverWait(browserCore, timeout / 1000);
        for (String xpath : xpathArray) {
            try {
                WebElement element = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                if (null != element && element.isDisplayed()) {
                    flag = true;
                    break;
                }
            } catch (NoSuchElementException e) {
                continue;
            } catch (Exception e) {
                break;
            }
        }
        if (expectExist) {
            if (flag) {
                logger.info("Found desired element [" + StringUtils.join(xpathArray, ",") + "]");
            } else {
                logger.error("Not found desired element [" + StringUtils.join(xpathArray, ",") + "]");
                handleFailure("Not found desired element [" + StringUtils.join(xpathArray, ",") + "]");
            }
        } else {
            if (!flag) {
                logger.info("Not found undesired element [" + StringUtils.join(xpathArray, ",") + "]");
            } else {
                logger.error("Found undesired element [" + StringUtils.join(xpathArray, ",") + "]");
                handleFailure("Found undesired element [" + StringUtils.join(xpathArray, ",") + "]");
            }
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isTextPresent(java.lang.String)
	 */
//    @Override
	public boolean isTextPresent(String text) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        Boolean flag = false;
        try {
            Boolean e = webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[contains(.,'" + text + "')]"), text));
            logger.info("Succeed text:{} and displayed", text);
            flag = e;
        } catch (Exception ex) {
            logger.error("Failed found text:{}, exception:{}", text, ex.getMessage());
            flag=false;
        }
        return flag ;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isElementPresent(java.lang.String)
	 */
    @Override
	public boolean isElementPresent(String xpath) {
        if (null!=findElement(xpath) && findElement(xpath).isDisplayed()) {
            logger.info("Found element " + xpath);
            return true;
        } else {
            logger.error("Not found element" + xpath);
            return false;
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#pause(int)
	 */
    @Override
	public void pause(int time) {
        if (time <= 0) {
            return;
        }
        try {
            Thread.sleep(time);
            logger.info("Pause " + time + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleFailure(String notice) {
        String png = LogTools.screenShot(this);
        String log = notice + " >> capture screenshot at " + png;
        logger.error(log);
        if (GlobalSettings.baseStorageUrl.lastIndexOf("/") == GlobalSettings.baseStorageUrl.length()) {
            GlobalSettings.baseStorageUrl = GlobalSettings.baseStorageUrl.substring(0, GlobalSettings.baseStorageUrl.length() - 1);
        }
        Reporter.log(log + "<br/><img src=\"" + GlobalSettings.baseStorageUrl + "/" + png + "\" />");
        Assert.fail(log);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getText(java.lang.String)
	 */
    @Override
	public String getText(String xpath) {
        WebElement element = findElement(xpath);
        return element.getAttribute("value");
    }

    public String getTableCellText(String xpath, int row, int col){
    	return getTableCellText(xpath,row, col,  0);
    }
    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getTableCellText(java.lang.String, int, int)
	 */
    @Override
	public String getTableCellText(String xpath,int row, int col, int waitTime) {

        //判断入参
        Assert.assertTrue((row>0&&col>0), "The input row and col is wrong!");

        //处理行列
        int realRow = row -1;
        int realCol = col -1;

        pause(waitTime);
        //得到table元素对象  
        WebElement table = findElementByXpath(xpath);

        //得到table表中所有行对象，并得到所要查询的行对象。  
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        WebElement theRow = rows.get(realRow);


        List<WebElement> cells;
        WebElement target = null;

        //列里面有"<th>"、"<td>"两种标签，所以分开处理。
        if(theRow.findElements(By.tagName("th")).size()>0){
            cells = theRow.findElements(By.tagName("th"));
            target = cells.get(realCol);
        }
        if(theRow.findElements(By.tagName("td")).size()>0){
            cells = theRow.findElements(By.tagName("td"));
            target = cells.get(realCol);
        }
        return target.getText();
    }
    
    public List<List<String>> getTableList(String xpath ) {
    	return getTableList(xpath , 0);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getTableList(java.lang.String)
	 */
    @Override
	public List<List<String>> getTableList(String xpath , int waitTime) {
    	pause(waitTime);

        //得到table元素对象
        WebElement table = findElementByXpath(xpath);

        //得到table表中所有行对象，并得到所要查询的行对象。
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        List<List<String>> tableDataList = new ArrayList<List<String>>();

        for(WebElement row: rows){
            List<WebElement> cols = row.findElements(By.tagName("td"));
            List<String> rowList = new ArrayList<String>();
            for(WebElement cell:cols){
                rowList.add(cell.getText());
            }
            tableDataList.add(rowList);
        }
        return tableDataList;
    }

    @Override
    public Map<String,Object> getTableSize(String xpath ) {
        return getTableSize(xpath , 0);
    }

    @Override
    public Map<String,Object> getTableSize(String xpath , int waitTime) {

        pause(waitTime);

        //得到table元素对象
        WebElement table = findElementByXpath(xpath);

        //得到table表中所有行对象，并得到所要查询的行对象。
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        Map<String,Object> sizeMap = new HashMap<String, Object>();

        if(null==rows){
            sizeMap.put("rows", 0);
            sizeMap.put("cols", 0);
        }else {
            List<WebElement> cols = rows.get(0).findElements(By.tagName("td"));
            sizeMap.put("rows", rows.size());
            sizeMap.put("cols", cols.size());
        }
        return sizeMap;
    }


    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#select(java.lang.String, java.lang.String)
	 */
    @Override
	public void select(String xpath, String option) {
        select(new String[] {xpath}, option);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#select(java.lang.String[], java.lang.String)
	 */
    @Override
	public void select(final String[] xpathArray, final String option) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.selectByVisibleText(option);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to select element by:{}", StringUtils.join(xpathArray, ","));
        }catch (Exception e){
            logger.error("Failed to select element by:{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to select");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#selectByIndex(java.lang.String, int)
	 */
    @Override
	public void selectByIndex(String xpath, int index) {
        selectByIndex(new String[]{xpath}, index);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#selectByIndex(java.lang.String[], int)
	 */
    @Override
	public void selectByIndex(final String[] xpathArray, final int index) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.selectByIndex(index);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to select by index using:{}", StringUtils.join(xpathArray, ","));
        }catch (TimeoutException e){
            logger.error("Failed to select by index using:{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to select by index");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#selectByValue(java.lang.String, java.lang.String)
	 */
    @Override
	public void selectByValue(String xpath, String value) {
        selectByValue(new String[]{xpath}, value);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#selectByValue(java.lang.String[], java.lang.String)
	 */
    @Override
	public void selectByValue(final String[] xpathArray, final String value) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.selectByValue(value);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to select by value using:{}", StringUtils.join(xpathArray, ","));
        }catch (TimeoutException e){
            logger.error("Failed to select by value using:{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to select by value");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#deSelect(java.lang.String, java.lang.String)
	 */
    @Override
	public void deSelect(String xpath, String option) {
        deSelect(new String[]{xpath}, option);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#deSelect(java.lang.String[], java.lang.String)
	 */
    @Override
	public void deSelect(final String[] xpathArray, final String option) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.deselectByVisibleText(option);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to deselect by:{}", StringUtils.join(xpathArray, ","));
        }catch (Exception e){
            logger.error("Failed to deselect by:{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to deselect ");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#deSelectByIndex(java.lang.String, int)
	 */
    @Override
	public void deSelectByIndex(String xpath, int index) {
        deSelectByIndex(new String[]{xpath}, index);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#deSelectByIndex(java.lang.String[], int)
	 */
    @Override
	public void deSelectByIndex(final String[] xpathArray, final int index) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.deselectByIndex(index);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to deselect by index using :{}", StringUtils.join(xpathArray, ","));
        }catch (Exception e){
            logger.error("Failed to deselect by index using :{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to deselect by index ");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#deSelectByValue(java.lang.String, java.lang.String)
	 */
    @Override
	public void deSelectByValue(String xpath, String value) {
        deSelectByValue(new String[]{xpath}, value);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#deSelectByValue(java.lang.String[], java.lang.String)
	 */
    @Override
	public void deSelectByValue(final String[] xpathArray, final String value) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.deselectByValue(value);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            flag = false;
                            handleFailure("Failed to deselect value " + value + " at " + xpath);
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to deselect by value using :{}", StringUtils.join(xpathArray, ","));
        }catch (Exception e){
            logger.error("Failed to deselect by value using xpath:{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to deselect by value ");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#clearSelection(java.lang.String)
	 */
    @Override
	public void clearSelection(String xpath) {
        clearSelection(new String[]{xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#clearSelection(java.lang.String[])
	 */
    @Override
	public void clearSelection(final String[] xpathArray) {
        webDriverWait = new WebDriverWait(browserCore, timeout/1000);
        try {
            webDriverWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    boolean flag = false;
                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = browserCore.findElement(By.xpath(xpath));
                            Select select = new Select(element);
                            if (select.isMultiple()) {
                                select.deselectAll();
                            } else {
                                select.selectByIndex(0);
                            }
                            flag = true;
                            break;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            });
            logger.info("Succeed to clear selection by value using :{}", StringUtils.join(xpathArray, ","));
        }catch (Exception e){
            logger.error("Failed to clear selection by value using xpath:{}, exception:{}", StringUtils.join(xpathArray, ","), e.getMessage());
            handleFailure("Failed to clear selection by value ");
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getAllOptions(java.lang.String)
	 */
    @Override
	public List<Map<String, String>> getAllOptions(String xpath) {
        WebElement element = findElement(xpath);
        Select select = new Select(element);
        List<WebElement> options = select.getOptions();
        List<Map<String, String>> optionsText = new ArrayList<Map<String, String>>();
        for (WebElement webElement : options) {
            HashMap<String, String> option = new HashMap<String, String>();
            option.put("value", webElement.getAttribute("value"));
            option.put("text", webElement.getText());
            optionsText.add(option);
        }
        return optionsText;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getAllSelectedOptions(java.lang.String)
	 */
    @Override
	public List<Map<String, String>> getAllSelectedOptions(String xpath) {
        WebElement element = findElement(xpath);
        Select select = new Select(element);

        List<WebElement> options = select.getAllSelectedOptions();

        List<Map<String, String>> optionsText = new ArrayList<Map<String, String>>();

        for (WebElement webElement : options) {
            HashMap<String, String> option = new HashMap<String, String>();

            option.put("value", webElement.getAttribute("value"));
            option.put("text", webElement.getText());

            optionsText.add(option);
        }

        return optionsText;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#uploadFile(java.lang.String, java.lang.String[])
	 */
    @Override
	public void uploadFile(final String filePath, final String[] xpathArray){
        new Thread(new Runnable() {
            public void run() {
                String exePath = this.getClass().getResource("/").toString().substring(6)+"software/upLoad.exe";
                Runtime rn = Runtime.getRuntime();
                try {
                    String command = exePath+" "+filePath;
                    rn.exec(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (GlobalSettings.browserCoreType != 3) {
            click(xpathArray);
        } else {
            webDriverWait = new WebDriverWait(browserCore, timeout/1000);
            try {
                webDriverWait.until(new ExpectedCondition <Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        boolean flag = false;

                        for (String xpath : xpathArray) {
                            try {
                                JavascriptExecutor js = getJavaScriptExecutor();
                                js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().click();");
                                flag = true;
                                break;
                            } catch (Exception e) {
                                logger.info("Failed to click file browser at xpath " + xpath, e);
                                flag = false;
                            }
                        }
                        return flag;
                    }
                });
                logger.info("Succeed to click file browser at :{},  exception:{}" + StringUtils.join(xpathArray, ","));
            } catch (Exception e) {
                logger.error("Failed to click file browser at :{},  exception:{}" + StringUtils.join(xpathArray, ","), e.getMessage());
                handleFailure("Failed to click file browser");
            }
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#clickAlert()
	 */
    @Override
	public void clickAlert(){
        Alert confirm = browserCore.switchTo().alert();
        confirm.accept();
    }

    private WebElement findElement(String xpath) {
        return this.getBrowserCore().findElement(By.xpath(xpath));
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#findElementByXpath(java.lang.String)
	 */
    @Override
	public WebElement findElementByXpath(final String xpath) {
        WebElement element = null;
        webDriverWait = new WebDriverWait(browserCore, timeout/1000 );
        try {
            element = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            logger.info("Found desired element [" + xpath + "]");

        } catch (Exception e) {
            logger.error("Fail to find element [" + xpath+ "], Cause:{}", e.getMessage());
            throw new TimeoutException(e.getMessage());
        }
        return  element;
    }

	@Override
	public String getCurrentUrl() {
        return browserCore.getCurrentUrl();
	}
}