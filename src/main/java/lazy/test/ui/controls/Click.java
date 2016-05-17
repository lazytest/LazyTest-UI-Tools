package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clickable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Click extends AbstractControl implements Clickable {
    public void click() {
        enterFrame();
        be.click(getXpath());
    }
}
