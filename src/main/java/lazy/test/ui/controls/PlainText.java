package lazy.test.ui.controls;

/**
 * Created by sushidong on 2016/4/13.
 */
public class PlainText extends AbstractControl {
	/**
	 * 进入iframe，找到控件
	 * 文本或文本列表，是否都存在于当前页面，只要有一个不存在，则返回false
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 *
	 */
	public boolean isExists() {
        enterFrame();
        return be.isTextExists(getTextContent());
    }
}
