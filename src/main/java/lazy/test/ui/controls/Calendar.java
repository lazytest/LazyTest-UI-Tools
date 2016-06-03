package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clearable;
import lazy.test.ui.interfaces.Inputable;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Calendar extends AbstractControl implements Inputable, Clearable {
	/**
	 * 进入iframe，找到控件
	 * 先清除日期，再填写日期
	 * 若日期格式不正确，抛异常
	 * 包含重试和超时机制
	 */
	public void input(String value) {
        enterFrame();
        be.calendarInput(getXpath(), value);
    }
	
	/**
	 * 进入iframe，找到控件
	 * 清除日期
	 * 若日期格式不正确，抛异常
	 * 包含重试和超时机制
	 */
    public void clear() {
        enterFrame();
        be.clear(getXpath());
    }
    
    /**
     * 进入iframe，找到控件
     * 返回控件的value（getAttribute("value")） 
     * 包含重试和超时机制
     */
    public String getText(){
        return be.getText(getValidXpath());
    }
}
