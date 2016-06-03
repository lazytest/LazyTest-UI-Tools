package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clickable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Click extends AbstractControl implements Clickable {
	/**
	 * 进入iframe，找到控件
	 * 点击控件
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
	public void click() {
        enterFrame();
        be.click(getXpath());
    }
}
