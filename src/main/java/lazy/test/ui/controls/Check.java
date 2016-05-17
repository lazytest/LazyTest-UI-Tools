package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Checkable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Check extends AbstractControl implements Checkable {
    public void check() {
        enterFrame();
        be.check(getXpath());
    }

    public void unCheck() {
        enterFrame();
        be.uncheck(getXpath());
    }

    @Override
    public boolean isChecked() {
        return be.isChecked(getValidXpath());
    }
}
