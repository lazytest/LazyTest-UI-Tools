package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clearable;
import lazy.test.ui.interfaces.Inputable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Text extends AbstractControl implements Inputable, Clearable {
	/**
	 * 进入iframe，找到控件
	 * 找到文本框或文本区域，先清除内容，再填写内容
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void input(String value) {
        enterFrame();
        be.type(getXpath(), value);
    }
    /**
     * 进入iframe，找到控件
	 * 找到文本框或文本区域，清除内容
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void clear() {
        enterFrame();
        be.clear(getXpath());
    }
    /**
     * 进入iframe，找到控件
	 * 返回文本控件的value（getAttribute("value")）
	 * 包含重试和超时机制
	 */
    public String getText(){
        return be.getText(getValidXpath());
    }
}
