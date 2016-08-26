package lazy.test.ui.exceptions;

/**
 * Created by sushidong on 2016/3/30.
 */
public class ElementNotFoundException extends RuntimeException {
    static final long serialVersionUID = -7034897190745766939L;
    public ElementNotFoundException(String msg) {
        super(msg);
    }
}
