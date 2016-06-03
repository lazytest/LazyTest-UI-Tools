package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Checkable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Check extends AbstractControl implements Checkable {
	/**
	 * 进入iframe，找到控件
	 * 勾选控件(radio, checkbox)，勾选失败则抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void check() {
        enterFrame();
        be.check(getXpath());
    }
    /**
	 * 进入iframe，找到控件
	 * 取消勾选控件(radio, checkbox)，勾选失败则抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void unCheck() {
        enterFrame();
        be.uncheck(getXpath());
    }
    
    /**
	 * 进入iframe，找到控件
	 * 查看是否勾选控件(radio, checkbox)
	 * 包含重试和超时机制
	 */
    public boolean isChecked() {
        return be.isChecked(getValidXpath());
    }
}
