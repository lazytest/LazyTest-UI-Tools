package lazy.test.ui.exceptions;

/**
 * Created by sushidong on 2016/3/30.
 */
public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String msg) {
        super(msg);
    }
}
