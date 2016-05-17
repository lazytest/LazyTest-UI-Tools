package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clearable;
import lazy.test.ui.interfaces.Inputable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Calendar extends AbstractControl implements Inputable, Clearable {
    public void input(String value) {
        enterFrame();
        be.calendarInput(getXpath(), value);
    }

    public void clear() {
        enterFrame();
        be.clear(getXpath());
    }

    public String getText(){
        return be.getText(getValidXpath());
    }
}
