package lazy.test.ui.controls;

import lazy.test.ui.browser.BrowseEmulator;
import lazy.test.ui.browser.GlobalSettings;
import lazy.test.ui.exceptions.ElementNotFoundException;

import com.thoughtworks.selenium.Wait;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sushidong on 2016/3/28.
 */
public abstract class AbstractControl {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected BrowseEmulator be;

    private String[] xpath;

    private static String validXpath;

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
                ", xpath=[" + StringUtils.join(xpath,",") + "]" +
                ", textContent=[" + StringUtils.join(textContent,",") + "]" +
                ", frame=" + frame +
                ", description=" + description +
                "}";
    }

    protected void enterFrame() {
        be.leaveFrame();

        if (!frame.equals("")) {
            be.enterFrameByNameOrId(frame);
        }
    }

    public String getValidXpath() {
        enterFrame();

        try {
            new Wait() {
                public boolean until() {
                    boolean flag = false;

                    for (String xpath : getXpath()) {
                        flag = be.isElementPresent(xpath);

                        if (flag) {
                            validXpath = xpath;
                            break;
                        }
                    }

                    return flag;
                }
            }.wait("Failed to find element [" + StringUtils.join(getXpath(), ",")+ "]", Integer.parseInt(GlobalSettings.timeout), Integer.parseInt(GlobalSettings.stepInterval));

            logger.info("Found desired element [" + StringUtils.join(getXpath(), ",")+ "]");
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw new ElementNotFoundException(e.getMessage());
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
