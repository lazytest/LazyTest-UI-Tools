package lazy.test.ui.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by sushidong on 2016/3/28.
 */
public interface Selectable {
    public void selectByVisibleText(String option);
    
    public void deselectByVisibleText(String option);

    public void selectByIndex(int index);

    public void deselectByIndex(int index);

    public void selectByValue(String value);

    public void deselectByValue(String value);

    public List<Map<String, String>> getAllOptions();

    public List<Map<String, String>> getAllSelectedOptions();

    public void clear();
}
