package lazy.test.ui.controls;

/**
 * Created by sushidong on 2016/4/13.
 */
public class PlainText extends AbstractControl {
    public boolean isExists() {
        enterFrame();

        return be.isTextExists(getTextContent());
    }
}
