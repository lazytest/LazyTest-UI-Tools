package lazy.test.ui.browser;

import com.thoughtworks.selenium.Wait;
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
public class BrowserEmulator {

	RemoteWebDriver browserCore;
	WebDriverBackedSelenium browser;
	ChromeDriverService chromeServer;
	JavascriptExecutor javaScriptExecutor;

    String chromeDriverPath;
    String ieDriverPath;
	
	int stepInterval = Integer.parseInt(GlobalSettings.stepInterval);
	int timeout = Integer.parseInt(GlobalSettings.timeout);
    int pause = Integer.parseInt(GlobalSettings.pause);
	
	private static Logger logger = LoggerFactory.getLogger(BrowserEmulator.class);

	public BrowserEmulator() {
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
	
	/**
	 * Get the WebDriver instance embedded in BrowserEmulator
	 * @return a WebDriver instance
	 */
	public RemoteWebDriver getBrowserCore() {
		return browserCore;
	}

	/**
	 * Get the WebDriverBackedSelenium instance embedded in BrowserEmulator
	 * @return a WebDriverBackedSelenium instance
	 */
	public WebDriverBackedSelenium getBrowser() {
		return browser;
	}
	
	/**
	 * Get the JavascriptExecutor instance embedded in BrowserEmulator
	 * @return a JavascriptExecutor instance
	 */
	public JavascriptExecutor getJavaScriptExecutor() {
		return javaScriptExecutor;
	}

	/**
	 * Open the URL
	 * @param url
	 *            the target URL
	 */
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

	/**
	 * Quit the browser
	 */
	public void quit() {
        pause(pause);
		browserCore.quit();
		if (GlobalSettings.browserCoreType == 2) {
			chromeServer.stop();
		}
		logger.info("Quitted BrowserEmulator");
	}

	/**
	 * Click the page element
	 * @param xpath
	 *            the element's xpath
	 */
	public void click(String xpath) {
		//expectElementExistOrNot(true, xpath, timeout);
		click(new String[] {xpath});
	}

    /**
     * Click the page element
     * @param xpath
     *            the element's xpath
     */
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

    /**
     * Check an element (radio, checkbox)
     * @param xpath
     */
    public void check(String xpath) {
        check(new String[] {xpath});
    }

    /**
     * Check an element (radio, checkbox)
     * @param xpathArray
     */
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

    public boolean isChecked(String xpath) {
        WebElement element = findElement(xpath);
        return Boolean.parseBoolean(element.getAttribute("checked"));
    }

    /**
     * Type text at the page element<br>
     * Before typing, try to clear existed text
     * @param xpath
     *            the element's xpath
     * @param text
     *            the input text
     */
    public void type(String xpath, String text) {
        type(new String[]{xpath},text);
    }

	/**
	 * Type text at the page element<br>
	 * Before typing, try to clear existed text
	 * @param xpathArray
	 *            the element's xpath
	 * @param text
	 *            the input text
	 */
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

    public void clear(String xpath) {
        clear(new String[] {xpath});
    }

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

    /**
     * Uncheck an element (radio, checkbox)
     * @param xpath
     */
    public void uncheck(String xpath) {
       uncheck(new String[] {xpath});
    }

    /**
     * Uncheck an element (radio, checkbox)
     * @param xpathArray
     */
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

    /**
     * Type date at the calendar control<br>
     * Before typing, try to clear default date
     * @param xpath
     *            the element's xpath
     * @param date
     *            the input date 'yyyy-MM-DD', eg:2016-02-01
     *
     * @author wyxufengyu
     */
    public void calendarInput(String xpath, String date) {
        calendarInput(new String[] {xpath}, date);
    }
	
	/**
	 * Type date at the calendar control<br>
	 * Before typing, try to clear default date
	 * @param xpathArray
	 *            the element's xpath
	 * @param date
	 *            the input date 'yyyy-MM-DD', eg:2016-02-01
	 * 
	 * @author wyxufengyu
	 */
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

    /**
     * Hover on the page element
     *
     * @param xpath
     *            the element's xpath
     */
    public void mouseOver(String xpath) {
        mouseOver(new String[] {xpath});
    }

	/**
	 * Hover on the page element
	 * 
	 * @param xpathArray
	 *            the element's xpath
	 */
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

	/**
	 * Switch window/tab
	 * @param windowTitle
	 *            the window/tab's title
	 */
	public void selectWindow(String windowTitle) {
		browser.selectWindow(windowTitle);
		logger.info("Switched to window " + windowTitle);
	}

	/**
	 * Enter the iframe
	 * @param xpath
	 *            the iframe's xpath
	 */
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

    /**
     * Enter frame
     * @param nameOrId frame's name or id
     */
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

	/**
	 * Leave the iframe
	 */
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
	
	/**
	 * Refresh the browser
	 */
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
	
	/**
	 * Mimic system-level keyboard event
	 * @param keyCode
	 *            such as KeyEvent.VK_TAB, KeyEvent.VK_F11
	 */
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

	/**
	 * Mimic system-level keyboard event with String
	 * 
	 * @param text
	 * 
	 */
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

    public boolean isTextExists(String text) {
        return isTextExists(new String[] {text});
    }

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

    /**
     * Expect some text exist or not on the page<br>
     * Expect text exist, but not found after timeout => Assert fail<br>
     * Expect text not exist, but found after timeout => Assert fail
     * @param expectExist
     *            true or false
     * @param text
     *            the expected text
     */
    public void expectTextExistOrNot(boolean expectExist, String text) {
        expectTextExistOrNot(expectExist, text, timeout);
    }

	/**
	 * Expect some text exist or not on the page<br>
	 * Expect text exist, but not found after timeout => Assert fail<br>
	 * Expect text not exist, but found after timeout => Assert fail
	 * @param expectExist
	 *            true or false
	 * @param text
	 *            the expected text
     * @param timeout
     *            timeout
	 */
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

    /**
     * Expect an element exist or not on the page<br>
     * Expect element exist, but not found after timeout => Assert fail<br>
     * Expect element not exist, but found after timeout => Assert fail<br>
     * Here <b>exist</b> means <b>visible</b>
     * @param expectExist
     *            true or false
     * @param xpath
     *            the expected element's xpath
     * @param timeout
     *            timeout in millisecond
     */
    public void expectElementExistOrNot(boolean expectExist, String xpath, int timeout) {
        expectElementExistOrNot(expectExist, new String[] {xpath}, timeout);
    }

	/**
	 * Expect an element exist or not on the page<br>
	 * Expect element exist, but not found after timeout => Assert fail<br>
	 * Expect element not exist, but found after timeout => Assert fail<br>
	 * Here <b>exist</b> means <b>visible</b>
	 * @param expectExist
	 *            true or false
	 * @param xpathArray
	 *            the expected element's xpath
	 * @param timeout
	 *            timeout in millisecond
	 */
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

	/**
	 * Is the text present on the page
	 * @param text
	 *            the expected text
	 * @return
	 */
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

	/**
	 * Is the element present on the page<br>
	 * Here <b>present</b> means <b>visible</b>
	 * @param xpath
	 *            the expected element's xpath
	 * @return
	 */
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
	
	/**
	 * Pause
	 * @param time in millisecond
	 */
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
	
	/**
	 * Return text from specified web element.
	 * @param xpath
	 * @return
	 */
	public String getText(String xpath) {
		WebElement element = findElement(xpath);
		return element.getAttribute("value");
	}
	
	
	/** 从一个table的单元格中得到文本值, 行列从1开始. 
    @param xpath  用于得到table对象 
    @param row,col 为了使用者便于
    @return 从一个table的单元格中得到文本值
    @author wyxufengyu
    */  
    public String getTableCellText(String xpath,int row, int col) {
    	
    	//判断入参
    	Assert.assertTrue((row>0&&col>0), "The input row and col is wrong!");
    	
    	//处理行列
    	int realRow = row -1;
    	int realCol = col -1;
    	
        //得到table元素对象  
        WebElement table = browserCore.findElement(By.xpath(xpath));  

        //得到table表中所有行对象，并得到所要查询的行对象。  
        pause(1000);
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        pause(1000);
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


    /** 从一个table的单元格中得到文本值
     @param xpath  用于得到table对象
     @return 从一个table的单元格中得到文本值
     @author wyxufengyu
     */
    public List<List<String>> getTableList(String xpath) {

        //得到table元素对象
        WebElement table = browserCore.findElement(By.xpath(xpath));

        //得到table表中所有行对象，并得到所要查询的行对象。
        pause(1000);
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

    /**
	 * Select an option by visible text from &lt;select&gt; web element.
	 * @param xpath
	 * @param option
	 * @throws Exception 
	 */
	public void select(String xpath, String option) {
		select(new String[] {xpath}, option);
	}

    /**
     * Select an option by visible text from &lt;select&gt; web element.
     * @param xpathArray
     * @param option
     * @throws Exception
     */
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

    /**
     * Select an option by index from &lt;select&gt; web element.
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void selectByIndex(String xpath, int index) {
        selectByIndex(new String[]{xpath}, index);
    }

    /**
     * Select an option by index from &lt;select&gt; web element.
     * @param xpathArray
     * @param index
     * @throws Exception
     */
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

    /**
     * Select an option by value from &lt;select&gt; web element.
     * @param xpath
     * @param value
     * @throws Exception
     */
    public void selectByValue(String xpath, String value) {
        selectByValue(new String[]{xpath}, value);
    }

    /**
     * Select an option by value from &lt;select&gt; web element.
     * @param xpathArray
     * @param value
     * @throws Exception
     */
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

    public void deSelect(String xpath, String option) {
        deSelect(new String[]{xpath}, option);
    }

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

    public void deSelectByIndex(String xpath, int index) {
        deSelectByIndex(new String[]{xpath}, index);
    }

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

    public void deSelectByValue(String xpath, String value) {
        deSelectByValue(new String[]{xpath}, value);
    }

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

    public void clearSelection(String xpath) {
        clearSelection(new String[]{xpath});
    }

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

    /**
     *
     * @Title: uploadFile
     * @Description: 文件上传,注意filePath必须是绝对路径
     * @param : filePath 文件路径
     * @param : xpathArray  控件的xpath
     * @return: void
     */
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


    public void clickAlert(){
		Alert confirm = browserCore.switchTo().alert();
		confirm.accept();
	}

    private WebElement findElement(String xpath) {
        return this.getBrowserCore().findElement(By.xpath(xpath));
    }
}
