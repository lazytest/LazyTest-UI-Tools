package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clearable;
import lazy.test.ui.interfaces.Selectable;

import java.util.List;
import java.util.Map;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Select extends AbstractControl implements Selectable, Clearable {
    public void selectByVisibleText(String option) {
        enterFrame();
        be.select(getXpath(), option);
    }

    public void deselectByVisibleText(String option) {
        enterFrame();
        be.deSelect(getXpath(), option);
    }

    public void selectByIndex(int index) {
        enterFrame();
        be.selectByIndex(getXpath(), index);
    }

    public void deselectByIndex(int index) {
        enterFrame();
        be.deSelectByIndex(getXpath(), index);
    }

    public void selectByValue(String value) {
        enterFrame();
        be.selectByValue(getXpath(), value);
    }

    public void deselectByValue(String value) {
        enterFrame();
        be.deSelectByValue(getXpath(), value);
    }

    public List<Map<String, String>> getAllOptions() {
        return be.getAllOptions(getValidXpath());
    }

    public List<Map<String, String>> getAllSelectedOptions() {
        return be.getAllSelectedOptions(getValidXpath());
    }

    public void clear() {
        enterFrame();
        be.clearSelection(getXpath());
    }
}
