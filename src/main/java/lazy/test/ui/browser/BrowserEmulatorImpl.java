package lazy.test.ui.browser;

import com.thoughtworks.selenium.Wait;
import lazy.test.ui.exceptions.ElementNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Select;
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

/**
 * BrowserEmulator is based on Selenium2 and adds some enhancements
 */
public class BrowserEmulatorImpl implements BrowserEmulator {

    RemoteWebDriver browserCore;
    WebDriverBackedSelenium browser;
    ChromeDriverService chromeServer;
    JavascriptExecutor javaScriptExecutor;

    String chromeDriverPath;
    String ieDriverPath;

    int stepInterval = Integer.parseInt(GlobalSettings.stepInterval);
    int timeout = Integer.parseInt(GlobalSettings.timeout);
    int pause = Integer.parseInt(GlobalSettings.pause);

    private static Logger logger = LoggerFactory.getLogger(BrowserEmulatorImpl.class);

    public BrowserEmulatorImpl() {
        chromeDriverPath = GlobalSettings.chromeDriverPath;
        ieDriverPath = GlobalSettings.ieDriverPath;

        if (!chromeDriverPath.startsWith("/")) {
            chromeDriverPath = "/" + chromeDriverPath;
        }

        if (!ieDriverPath.startsWith("/")) {
            ieDriverPath = "/" + ieDriverPath;
        }

        chromeDriverPath = this.getClass().getResource(chromeDriverPath).getPath();
        ieDriverPath = this.getClass().getResource(ieDriverPath).getPath();

        setupBrowserCoreType(GlobalSettings.browserCoreType);
        browser = new WebDriverBackedSelenium(browserCore, "http://www.163.com/");
        javaScriptExecutor = (JavascriptExecutor) browserCore;
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

        Assert.fail("Incorrect browser type");
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getBrowserCore()
	 */
    @Override
	public RemoteWebDriver getBrowserCore() {
        return browserCore;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getBrowser()
	 */
    @Override
	public WebDriverBackedSelenium getBrowser() {
        return browser;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getJavaScriptExecutor()
	 */
    @Override
	public JavascriptExecutor getJavaScriptExecutor() {
        return javaScriptExecutor;
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#open(java.lang.String)
	 */
    @Override
	public void open(String url) {
        pause(pause);
        try {
            browser.open(url);
        } catch (Exception e) {
            e.printStackTrace();
            handleFailure("Failed to open url " + url);
        }
        logger.info("Opened url " + url);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#quit()
	 */
    @Override
	public void quit() {
        pause(pause);
        browserCore.quit();
        if (GlobalSettings.browserCoreType == 2) {
            chromeServer.stop();
        }
        logger.info("Quitted BrowserEmulator");
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#click(java.lang.String)
	 */
    @Override
	public void click(String xpath) {
        //expectElementExistOrNot(true, xpath, timeout);
        click(new String[] {xpath});
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#click(java.lang.String[])
	 */
    @Override
	public void click(String[] xpath) {
        pause(pause);
        //expectElementExistOrNot(true, xpath, timeout);
        try {
            clickTheClickable(xpath);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
        logger.info("Clicked [" + StringUtils.join(xpath, ",") + "]");
    }

    /**
     * Click an element until it's clickable or timeout
     * @param xpathArray
     * @throws Exception
     */
    private void clickTheClickable(final String[] xpathArray) throws Exception {
        new Wait() {
            public boolean until() {
                boolean flag = false;

                for (String xpath : xpathArray) {
                    try {
                        findElement(xpath).click();
                        flag =  true;
                        break;
                    } catch (Exception e) {
                        logger.info("Click failed, xpath = " + xpath, e);
                        flag = false;
                    }
                }

                return flag;
            }
        }.wait("Timeout when click [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();

                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().checked=true;");

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Check failed, xpath = " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when check [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);
        //expectElementExistOrNot(true, xpath, timeout);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement we = findElement(xpath);
                            we.clear();
                            we.sendKeys(text);

                            logger.info("Type " + text + " at " + xpath);
                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to type " + text + " at [" + xpath + "]");
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when type " + text + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            logger.info("", e);
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();

                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().value='';");

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to clear at xpath " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when clear at xpath [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();

                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().checked=false;");

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to uncheck at xpath " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when uncheck at xpath [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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

        pause(pause);
        //expectElementExistOrNot(true, xpath, timeout);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            JavascriptExecutor js = getJavaScriptExecutor();

                            js.executeScript("document.evaluate(\"" + xpath + "\",document,null,XPathResult.ANY_TYPE,null).iterateNext().value='" + date + "';");

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to type date at xpath " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when type date at xpath [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }

        logger.info("Type " + date + " at [" + StringUtils.join(xpathArray, ",") + "]");
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

        // Selenium doesn't support the Safari browser
        if (GlobalSettings.browserCoreType == 4) {
            Assert.fail("Mouseover is not supported for Safari now");
            Assert.fail("Incorrect browser type");
        }

        pause(pause);
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
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        // Then hover
                        WebElement we = findElement(xpath);

                        if (GlobalSettings.browserCoreType == 2) {
                            try {
                                Actions builder = new Actions(browserCore);
                                builder.moveToElement(we).build().perform();
                                flag = true;
                                break;
                            } catch (Exception e) {
                                logger.info("Failed to mouseover " + xpath, e);
                                flag = false;
                            }

                            logger.info("Mouseover " + xpath);
                        }

                        // Firefox and IE require multiple cycles, more than twice, to cause a
                        // hovering effect
                        if (GlobalSettings.browserCoreType == 1
                                || GlobalSettings.browserCoreType == 3) {
                            try {
                                for (int i = 0; i < 5; i++) {
                                    Actions builder = new Actions(browserCore);
                                    builder.moveToElement(we).build().perform();
                                }
                                logger.info("Mouseover " + xpath);
                                flag = true;
                                break;
                            } catch (Exception e) {
                                logger.info("Failed to mouseover " + xpath, e);
                                flag = false;
                            }

                            logger.info("Mouseover " + xpath);
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when mouseover on xpath [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#selectWindow(java.lang.String)
	 */
    @Override
	public void selectWindow(String windowTitle) {
        browser.selectWindow(windowTitle);
        logger.info("Switched to window " + windowTitle);
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#enterFrame(java.lang.String)
	 */
    @Override
	public void enterFrame(final String xpath) {
        //browserCore.switchTo().frame(findElement(xpath));
        //logger.info("Entered iframe " + xpath);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag;
                    try {
                        //pause(stepInterval);
                        browserCore.switchTo().frame(findElement(xpath));
                        logger.info("Entered iframe " + xpath);

                        flag = true;
                    } catch (Exception e) {
                        logger.info("Entered iframe " + xpath + " failed");
                        flag = false;
                    }

                    return flag;
                }
            }.wait("Timeout when enter iframe " + xpath, timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#enterFrameByNameOrId(java.lang.String)
	 */
    @Override
	public void enterFrameByNameOrId(final String nameOrId) {
        try {
            new Wait() {
                public boolean until() {
                    boolean flag;
                    try {
                        browserCore.switchTo().frame(nameOrId);

                        flag = true;

                        logger.info("Entered iframe " + nameOrId);
                    } catch (Exception e) {
                        logger.info("Entered iframe " + nameOrId + " failed");
                        flag = false;
                    }

                    return flag;
                }
            }.wait("Timeout when enter iframe " + nameOrId, timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#leaveFrame()
	 */
    @Override
	public void leaveFrame() {
        //pause(stepInterval);
        //browserCore.switchTo().defaultContent();
        //logger.info("Left the iframe");

        try {
            new Wait() {
                public boolean until() {
                    boolean flag;
                    try {
                        //pause(stepInterval);
                        browserCore.switchTo().defaultContent();
                        logger.info("Left the iframe");

                        flag = true;
                    } catch (Exception e) {
                        logger.info("Failed to leave iframe");
                        flag = false;
                    }

                    return flag;
                }
            }.wait("Timeout when leave iframe", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#refresh()
	 */
    @Override
	public void refresh() {
        //pause(stepInterval);
        //browserCore.navigate().refresh();
        //logger.info("Refreshed");

        try {
            new Wait() {
                public boolean until() {
                    boolean flag;
                    try {
                        //pause(stepInterval);
                        browserCore.navigate().refresh();
                        logger.info("Refreshed");

                        flag = true;
                    } catch (Exception e) {
                        logger.info("Failed to refresh");
                        flag = false;
                    }

                    return flag;
                }
            }.wait("Timeout when refresh", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#pressKeyboard(int)
	 */
    @Override
	public void pressKeyboard(final int keyCode) {
        pause(pause);
        try {
            new Wait() {
                public boolean until() {
                    boolean flag;
                    try {
                        Robot rb = new Robot();
                        rb.keyPress(keyCode);	// press key
                        rb.delay(100); 			// delay 100ms
                        rb.keyRelease(keyCode);	// release key
                        logger.info("Pressed key with code " + keyCode);

                        flag = true;
                    } catch (Exception e) {
                        logger.info("Failed to press key with code " + keyCode);
                        flag = false;
                    }

                    return flag;
                }
            }.wait("Timeout when press key with code " + keyCode, timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#inputKeyboard(java.lang.String)
	 */
    @Override
	public void inputKeyboard(final String text) {
        pause(pause);
        try {
            new Wait() {
                public boolean until() {
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
            }.wait("Timeout when press key with string " + text, timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String text : textArray) {
                        flag = isTextPresent(text);

                        if (!flag) {
                            break;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout to find text [" + StringUtils.join(textArray, ",") + "]", timeout, stepInterval);
            return true;
        } catch (Exception e) {
            logger.info("Not all text [" + StringUtils.join(textArray, ",") + "] exists", e);
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
	public void expectTextExistOrNot(boolean expectExist, final String text, int timeout) {
        if (expectExist) {
            try {
                new Wait() {
                    public boolean until() {
                        return isTextPresent(text);
                    }
                }.wait("Failed to find text " + text, timeout, stepInterval);
            } catch (Exception e) {
                logger.info(e.getMessage(),e);
                handleFailure("Failed to find text " + text);
            }
            logger.info("Found desired text " + text);
        } else {
			/*if (isTextPresent(text)) {
				handleFailure("Found undesired text " + text);
			} else {
				logger.info("Not found undesired text " + text);
			}*/

            try {
                new Wait() {
                    public boolean until() {
                        return !isTextPresent(text);
                    }
                }.wait("Found undesired text " + text, timeout, stepInterval);
            } catch (Exception e) {
                logger.info(e.getMessage(),e);
                handleFailure("Found undesired text " + text);
            }
            logger.info("Not found undesired text " + text);
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
        if (expectExist) {
            try {
                new Wait() {
                    public boolean until() {
                        boolean flag = false;

                        for (String xpath : xpathArray) {
                            flag = isElementPresent(xpath);

                            if (flag) {
                                break;
                            }
                        }

                        return flag;
                    }
                }.wait("Failed to find element [" + StringUtils.join(xpathArray, ",")+ "]", timeout, stepInterval);

                logger.info("Found desired element [" + StringUtils.join(xpathArray, ",")+ "]");
            } catch (Exception e) {
                logger.info(e.getMessage(),e);
                handleFailure(e.getMessage());
            }
        } else {
            try {
                new Wait() {
                    public boolean until() {
                        boolean flag = false;

                        for (String xpath : xpathArray) {
                            flag = !isElementPresent(xpath);

                            if (!flag) {
                                break;
                            }
                        }

                        return flag;
                    }
                }.wait("Failed to find element [" + StringUtils.join(xpathArray, ",")+ "]", timeout, stepInterval);

                logger.info("Not found undesired element [" + StringUtils.join(xpathArray, ",")+ "]");
            } catch (Exception e) {
                logger.info(e.getMessage(),e);
                handleFailure("Found undesired element [" + StringUtils.join(xpathArray, ",")+ "]");
            }

            /*if (isElementPresent(xpath, stepInterval)) {
                if (tried == GlobalSettings.retry) {
                    handleFailure("Found undesired element " + xpath);
                }

                pause(stepInterval);
                logger.info("Found undesired element " + xpath + ", retry " + (++tried));
            } else {
                logger.info("Not found undesired element " + xpath);
                break;
            }*/
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isTextPresent(java.lang.String)
	 */
    @Override
	public boolean isTextPresent(String text) {
        //pause(time);
        boolean isPresent = browser.isTextPresent(text);
        if (isPresent) {
            logger.info("Found text " + text);
            return true;
        } else {
            logger.info("Not found text " + text);
            return false;
        }
    }

    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#isElementPresent(java.lang.String)
	 */
    @Override
	public boolean isElementPresent(String xpath) {
        //pause(time);
        boolean isPresent = browser.isElementPresent(xpath) && findElement(xpath).isDisplayed();
        if (isPresent) {
            logger.info("Found element " + xpath);
            return true;
        } else {
            logger.info("Not found element" + xpath);
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


    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getTableCellText(java.lang.String, int, int)
	 */
    @Override
	public String getTableCellText(String xpath,int row, int col) {

        //判断入参
        Assert.assertTrue((row>0&&col>0), "The input row and col is wrong!");

        //处理行列
        int realRow = row -1;
        int realCol = col -1;

        //得到table元素对象  
        WebElement table = findElementByXpath(xpath);

        //得到table表中所有行对象，并得到所要查询的行对象。  
//        pause(1000);
        List<WebElement> rows = table.findElements(By.tagName("tr"));
//        pause(1000);
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


    /* (non-Javadoc)
	 * @see lazy.test.ui.browser.BrowseEmulator#getTableList(java.lang.String)
	 */
    @Override
	public List<List<String>> getTableList(String xpath) {

        //得到table元素对象
        WebElement table = findElementByXpath(xpath);

        //得到table表中所有行对象，并得到所要查询的行对象。
//        pause(1000);
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.selectByVisibleText(option);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to select " + option + " at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when select " + option + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.selectByIndex(index);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to select index " + index + " at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when select index " + index + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.selectByValue(value);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to select value " + value + " at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when select value " + value + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.deselectByVisibleText(option);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to deselect " + option + " at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when deselect " + option + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.deselectByIndex(index);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to deselect index " + index + " at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when deselect index " + index + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : xpathArray) {
                        try {
                            WebElement element = findElement(xpath);
                            Select select = new Select(element);
                            select.deselectByValue(value);

                            flag = true;
                            break;
                        } catch (Exception e) {
                            logger.info("Failed to deselect value " + value + " at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when deselect value " + value + " at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
        pause(pause);

        try {
            new Wait() {
                public boolean until() {
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
                            logger.info("Failed to clear selection at " + xpath, e);
                            flag = false;
                        }
                    }

                    return flag;
                }
            }.wait("Timeout when clear selection at [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
        } catch (Exception e) {
            handleFailure(e.getMessage());
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
            try {
                new Wait() {
                    public boolean until() {
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
                }.wait("Timeout when click file browser at xpath [" + StringUtils.join(xpathArray, ",") + "]", timeout, stepInterval);
            } catch (Exception e) {
                handleFailure(e.getMessage());
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
        try {
            new Wait() {
                public boolean until() {
                    return isElementPresent(xpath);
                }
            }.wait("Failed to find element [" + xpath+ "]", Integer.parseInt(GlobalSettings.timeout), Integer.parseInt(GlobalSettings.stepInterval));

            logger.info("Found desired element [" + xpath+ "]");
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw new ElementNotFoundException(e.getMessage());
        }

        return this.getBrowserCore().findElement(By.xpath(xpath));
    }

	@Override
	public String getCurrentUrl() {
		return browser.getWrappedDriver().getCurrentUrl();
	}
}
