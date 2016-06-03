package lazy.test.ui.controls;

import lazy.test.ui.interfaces.Clearable;
import lazy.test.ui.interfaces.Selectable;

import java.util.List;
import java.util.Map;

/**
 * Created by sushidong on 2016/3/28.
 */
public class Select extends AbstractControl implements Selectable, Clearable {
	/**
	 * 进入iframe，找到控件
	 * 找到下拉菜单，并根据展示的文字（VisibleText），选中该选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
	public void selectByVisibleText(String option) {
        enterFrame();
        be.select(getXpath(), option);
    }
	
	/**
	 * 进入iframe，找到控件
	 * 找到下拉菜单，并根据展示的文字（VisibleText），选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void deselectByVisibleText(String option) {
        enterFrame();
        be.deSelect(getXpath(), option);
    }
    
    /**
	 * 进入iframe，找到控件
	 * 找到下拉菜单，并根序号，选中该选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void selectByIndex(int index) {
        enterFrame();
        be.selectByIndex(getXpath(), index);
    }
    
    /**
	 * 进入iframe，找到控件
	 * 找到下拉菜单，并根序号，选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void deselectByIndex(int index) {
        enterFrame();
        be.deSelectByIndex(getXpath(), index);
    }

    /**
   	 * 进入iframe，找到控件
   	 * 找到下拉菜单，并根据值（value），选中该选项
   	 * 包含重试和超时机制
   	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
   	 */
    public void selectByValue(String value) {
        enterFrame();
        be.selectByValue(getXpath(), value);
    }

    /**
   	 * 进入iframe，找到控件
   	 * 找到下拉菜单，并根据值（value），选择除该选项之外的所有选项
   	 * 包含重试和超时机制
   	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
   	 */
    public void deselectByValue(String value) {
        enterFrame();
        be.deSelectByValue(getXpath(), value);
    }

    /**
     * 进入iframe，找到控件
	 * 获取下拉菜单的所有可选项
	 * 包含重试和超时机制
	 * 返回MAP：
	 * 			"value", webElement.getAttribute("value")
	 *			"text", webElement.getText()
	 */
    public List<Map<String, String>> getAllOptions() {
        return be.getAllOptions(getValidXpath());
    }

    /**
     * 进入iframe，找到控件
	 * 获取下拉菜单的所有已选择的选项
	 * 包含重试和超时机制
	 * 返回MAP：
	 * 			"value", webElement.getAttribute("value")
	 *			"text", webElement.getText()
	 */
    public List<Map<String, String>> getAllSelectedOptions() {
        return be.getAllSelectedOptions(getValidXpath());
    }
    
    /**
     * 进入iframe，找到控件
	 * 清除下拉菜单为初始状态
	 * 如果是多选的，则全部不选
	 * 如果是单选的，则选中序号为0的
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
    public void clear() {
        enterFrame();
        be.clearSelection(getXpath());
    }
}
