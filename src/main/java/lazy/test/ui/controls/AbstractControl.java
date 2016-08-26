package lazy.test.ui.controls;

import lazy.test.ui.browser.BrowserEmulator;
import lazy.test.ui.browser.GlobalSettings;
import lazy.test.ui.exceptions.ElementNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sushidong on 2016/3/28.
 */
public abstract class AbstractControl {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected BrowserEmulator be;

    private String[] xpath;

    private String validXpath;

    private String[] textContent;

    private String frame;

    private String description;

    public String[] getXpath() {
        return xpath;
    }

    public void setXpath(String[] xpath) {
        this.xpath = xpath;
    }

    public String[] getTextContent() {
        return textContent;
    }

    public void setTextContent(String[] textContent) {
        this.textContent = textContent;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ControlType getType() {
        return ControlType.valueOf(this.getClass().getSimpleName().toUpperCase());
    }

    public String toString() {
        return "Control{type=" + this.getClass().getSimpleName() +
                ", xpath=[" + StringUtils.join(xpath, ",") + "]" +
                ", textContent=[" + StringUtils.join(textContent, ",") + "]" +
                ", frame=" + frame +
                ", description=" + description +
                "}";
    }

    public WebElement getWebElement(String xpath) {
        WebDriverWait webDriverWait = new WebDriverWait(be.getBrowserCore(), 3);
        WebElement element = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        return element;
    }

    protected void enterFrame() {
        be.leaveFrame();

        if (!frame.equals("")) {
            be.enterFrameByNameOrId(frame);
        }
    }

    public String getValidXpath() {
        enterFrame();
        String[] xpathArr = getXpath();
        WebDriverWait webDriverWait = new WebDriverWait(be.getBrowserCore(), 3);
        for (int i = 0; i < xpathArr.length; i++) {
            try {
                WebElement res = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathArr[i])));
                if (null != res) {
                    validXpath = xpathArr[i];
                    break;
                }
            } catch (Exception e) {
                if (i == xpathArr.length - 1) {
                    logger.error(e.getMessage());
                    throw new ElementNotFoundException(e.getMessage());
                }
            }
        }
        return validXpath;
    }

    public void expectElementExists() {
        be.expectElementExistOrNot(true, getXpath(), Integer.parseInt(GlobalSettings.timeout));
    }

    public void expectElementNotExists() {
        be.expectElementExistOrNot(false, getXpath(), Integer.parseInt(GlobalSettings.timeout));
    }

    public boolean isExists() {
        try {
            getValidXpath();

            return true;
        } catch (ElementNotFoundException e) {
            return false;
        }
    }
}