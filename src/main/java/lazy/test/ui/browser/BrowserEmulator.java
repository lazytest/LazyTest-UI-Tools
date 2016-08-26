package lazy.test.ui.browser;
/**
 * Created by yangyang on 2016/6/3.
 */

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

//import org.openqa.selenium.WebDriverBackedSelenium;

public interface BrowserEmulator {

	/**
	 * Get the WebDriver instance embedded in BrowserEmulator
	 * 获取浏览器仿真器实例
	 * @return a WebDriver instance
	 */
	RemoteWebDriver getBrowserCore();

	/**
	 * Get the WebDriverBackedSelenium instance embedded in BrowserEmulator
	 * 获取selenium控制的浏览器实例
	 * @return a WebDriverBackedSelenium instance
	 */
//	WebDriverBackedSelenium getBrowser();

	/**
	 * Get the WebDriver Wait instance embedded in BrowserEmulator
	 * 获取当前WebDriver使用的WebDriverWait
	 * @return WebDriverWait instance;
	 */
	WebDriverWait getWebDriverWait();
	/**
	 * Get the JavascriptExecutor instance embedded in BrowserEmulator
	 * 获取JavaScript执行器
	 * @return a JavascriptExecutor instance
	 */
	JavascriptExecutor getJavaScriptExecutor();
	
	/**
	 * 返回当前聚焦的window的title
	 */
	String getCurrentWindowTitle();
	
	/**
	 * Open the URL
	 * 打开一个URL
	 * @param url
	 * the target URL
	 */
	void open(String url);

	/**
	 * Quit the browser
	 * 退出浏览器
	 */
	void quit();

	/**
	 * Click the page element
	 * 根据xpath找到控件并点击
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * the element's xpath
	 */
	void click(String xpath);

	/**
	 * Click the page element
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到控件并点击
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * the element's xpath
	 */
	void click(String[] xpath);

	/**
	 * Check an element (radio, checkbox)
	 * 根据xpath勾选控件(radio, checkbox)，勾选失败则抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 */
	void check(String xpath);

	/**
	 * Check an element (radio, checkbox)
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），勾选控件(radio, checkbox)，勾选失败则抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 */
	void check(String[] xpathArray);

	
	/**
	 * Uncheck an element (radio, checkbox)
	 * 根据xpath取消勾选控件(radio, checkbox)，勾选失败则抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 */
	void uncheck(String xpath);

	/**
	 * Uncheck an element (radio, checkbox)
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），取消勾选控件(radio, checkbox)，勾选失败则抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 */
	void uncheck(String[] xpathArray);
	
	/**
	 * ischecked an element (radio, checkbox)
	 * 根据xpath，返回控件是否被勾选(radio, checkbox)
	 * 不包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param xpath
	 */
	boolean isChecked(String xpath);
	
	
	/**
	 * Type text at the page element<br>
	 * Before typing, try to clear existed text
	 * 根据xpath，找到一个文本框或文本区域，先清除内容，再填写内容
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 *            the element's xpath
	 * @param text
	 *            the input text
	 */
	void type(String xpath, String text);

	/**
	 * Type text at the page element<br>
	 * Before typing, try to clear existed text
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），找到一个文本框或文本区域，先清除内容，再填写内容
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 *            the element's xpath
	 * @param text
	 *            the input text
	 */
	
	void type(String[] xpathArray, String text);

	/**
	 * 根据xpath，找到一个文本框或文本区域，清除内容
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 *            the element's xpath
	 */
	void clear(String xpath);
	
	/**
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），找到一个文本框或文本区域，清除内容
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 *            the element's xpath
	 */
	void clear(String[] xpathArray);

	

	/**
	 * Type date at the calendar control<br>
	 * Before typing, try to clear default date
	 * 根据xpath，找到一个日历控件（仅支持原生控件），先清除日期，再填写日期
	 * 若日期格式不正确，抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 *            the element's xpath
	 * @param date
	 *            the input date 'yyyy-MM-DD', eg:2016-02-01
	 *
	 * @author wyxufengyu
	 */
	void calendarInput(String xpath, String date);

	/**
	 * Type date at the calendar control<br>
	 * Before typing, try to clear default date
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），找到一个日历控件（仅支持原生控件），先清除日期，再填写日期
	 * 若日期格式不正确，抛异常
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 *            the element's xpath
	 * @param date
	 *            the input date 'yyyy-MM-DD', eg:2016-02-01
	 *
	 * @author wyxufengyu
	 */
	void calendarInput(String[] xpathArray, String date);

	/**
	 * Hover on the page element
	 * 根据xpath，找到一个控件，鼠标移动到控件上方
	 * 不支持Safari
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 *            the element's xpath
	 */
	void mouseOver(String xpath);

	/**
	 * Hover on the page element
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），找到一个控件，鼠标移动到控件上方
	 * 不支持Safari
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 *            the element's xpath
	 */
	void mouseOver(String[] xpathArray);

	/**
	 * Switch window/tab
	 * 根据title来切换浏览器的选项卡
	 * 如果有重名的，就不好使了
	 * 不包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param windowTitle
	 *            the window/tab's title
	 * @throws Exception 
	 */
	void selectWindow(String windowTitle);
	
	/**
	 * Switch window/tab
	 * 根据title中包含的字符串来切换浏览器的选项卡
	 * 如果有重名的，默认切换到找到的第一个
	 * 不包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param windowTitle
	 *            the window/tab's title
	 * @throws Exception 
	 */
	void selectWindowFuzzy(String windowTitleWord);
	
	/**
	 * Switch window/tab
	 * 切换到driver打开的唯一的window，通常关掉其他窗口后，需要切换回原窗口（driver只剩原窗口）时使用
	 * @throws Exception 
	 */
	void selectTheOnlyWindow();
	
	/**
	 * Enter the iframe
	 * 根据xpath，进入一个iframe
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param xpath
	 *            the iframe's xpath
	 */
	void enterFrame(String xpath);

	/**
	 * Enter frame
	 * 根据nameOrId，进入一个iframe
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param nameOrId frame's name or id
	 */
	void enterFrameByNameOrId(String nameOrId);

	/**
	 * Leave the iframe
	 * 离开当前iframe
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 */
	void leaveFrame();

	/**
	 * Refresh the browser
	 * 刷新当前页面
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 */
	void refresh();

	/**
	 * Mimic system-level keyboard event
	 * @param keyCode
	 * 模拟按下一个键，等待100毫秒，释放该键
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 *            such as KeyEvent.VK_TAB, KeyEvent.VK_F11
	 */
	void pressKeyboard(int keyCode);

	/**
	 * Mimic system-level keyboard event with String
	 * 模拟键盘输入一段文字
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param text
	 *
	 */
	void inputKeyboard(String text);
	
	/**
	 * 查找输入字符串是否存在于当前页面
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param text
	 *
	 */
	boolean isTextExists(String text);
	
	/**
	 * 查找输入字符串列表，是否都存在于当前页面，只要有一个不存在，则返回false
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param text
	 *
	 */
	boolean isTextExists(String[] textArray);

	/**
	 * Expect some text exist or not on the page<br>
	 * Expect text exist, but not found after timeout => Assert fail<br>
	 * Expect text not exist, but found after timeout => Assert fail
	 * 查找输入字符串是否存在于当前页面
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param expectExist
	 *            true or false
	 * @param text
	 *            the expected text
	 */
	void expectTextExistOrNot(boolean expectExist, String text);

	/**
	 * Expect some text exist or not on the page<br>
	 * Expect text exist, but not found after timeout => Assert fail<br>
	 * Expect text not exist, but found after timeout => Assert fail
	 * 查找输入字符串是否存在于当前页面，可自定义超时时间
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param expectExist
	 *            true or false
	 * @param text
	 *            the expected text
	 * @param timeout
	 *            timeout
	 */
	void expectTextExistOrNot(boolean expectExist, String text, int timeout);

	/**
	 * Expect an element exist or not on the page<br>
	 * Expect element exist, but not found after timeout => Assert fail<br>
	 * Expect element not exist, but found after timeout => Assert fail<br>
	 * Here <b>exist</b> means <b>visible</b>
	 * 根据Xpath查找控件是否在当前页面可见
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param expectExist
	 *            true or false
	 * @param xpath
	 *            the expected element's xpath
	 * @param timeout
	 *            timeout in millisecond
	 */
	void expectElementExistOrNot(boolean expectExist, String xpath, int timeout);

	/**
	 * Expect an element exist or not on the page<br>
	 * Expect element exist, but not found after timeout => Assert fail<br>
	 * Expect element not exist, but found after timeout => Assert fail<br>
	 * Here <b>exist</b> means <b>visible</b>
	 * 根据Xpath查找控件是否在当前页面可见，可自定义超时时间
	 * 包含重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param expectExist
	 *            true or false
	 * @param xpathArray
	 *            the expected element's xpath
	 * @param timeout
	 *            timeout in millisecond
	 */
	void expectElementExistOrNot(boolean expectExist, String[] xpathArray, int timeout);

	/**
	 * Is the text present on the page
	 * 判断字符串是否在当前页面存在并可见
	 * 无重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param text
	 *            the expected text
	 * @return
	 */
	boolean isTextPresent(String text);

	/**
	 * Is the element present on the page<br>
	 * Here <b>present</b> means <b>visible</b>
	 * 根据Xpath 查找控件是否在当前页面存在并可见
	 * 无重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * @param xpath
	 *            the expected element's xpath
	 * @return
	 */
	boolean isElementPresent(String xpath);

	/**
	 * Pause
	 * 等待XXX毫秒
	 * @param time in millisecond
	 */
	void pause(int time);

	/**
	 * Return text from specified web element.
	 * 根据Xpath，查找控件的value（getAttribute("value")）
	 * 无重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * 
	 * @param xpath
	 * @return
	 */
	String getText(String xpath);

	/** 
	 * 从table中指定的的单元格中得到文本值, 行列从1开始.
	 * 可设置等待时间，以便table完全加载完成
	 * @param xpath  用于得到table对象
	 * @param row,col 为了使用者便于
	 * @return 单元格中的文本值
	 */
	String getTableCellText(String xpath, int row, int col, int waitTime);
	
	/** 
	 * 从table中指定的的单元格中得到文本值, 行列从1开始.
	 * @param xpath  用于得到table对象
	 * @param row,col 为了使用者便于
	 * @return 单元格中的文本值
	 */
	String getTableCellText(String xpath, int row, int col);

	/** 
	 * 得到table中所有单元格的文本值
	 * @param xpath  用于得到table对象
	 *@return 单元格中的文本值列表
	 */
	List<List<String>> getTableList(String xpath);

	/** 
	 * 得到table中所有单元格的文本值
	 * 可设置等待时间，以便table完全加载完成
	 * @param xpath  用于得到table对象
	 * @return 单元格中的文本值列表
	 */
	List<List<String>> getTableList(String xpath , int waitTime);


    /**
     * 得到table的行列长度
     * @param xpath  用于得到table对象
     *@return <"rows", m>
     *        <"cols", n>
     */
    Map<String,Object> getTableSize (String xpath);

    /**
     * 得到table的行列长度
     * 可设置等待时间，以便table完全加载完成
     * @param xpath  用于得到table对象
     *@return <"rows", m>
     *        <"cols", n>
     */
    Map<String,Object> getTableSize (String xpath, int waitTime);

	/**
	 * Select an option by visible text from &lt;select&gt; web element.
	 * 根据Xpath找到下拉菜单，并根据展示的文字（VisibleText），选择其中一个选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param option
	 * @throws Exception
	 */
	void select(String xpath, String option);

	/**
	 * Select an option by visible text from &lt;select&gt; web element.
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到下拉菜单，并根据展示的文字（VisibleText），选择其中一个选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 * @param option
	 * @throws Exception
	 */
	void select(String[] xpathArray, String option);

	/**
	 * Select an option by index from &lt;select&gt; web element.
	 * 根据Xpath找到下拉菜单，并根序号，选择其中一个选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param index
	 * @throws Exception
	 */
	void selectByIndex(String xpath, int index);

	/**
	 * Select an option by index from &lt;select&gt; web element.
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到下拉菜单，并根据序号，选择其中一个选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 * @param index
	 * @throws Exception
	 */
	void selectByIndex(String[] xpathArray, int index);

	/**
	 * Select an option by value from &lt;select&gt; web element.
	 * 根据Xpath找到下拉菜单，并根据值（value），选择其中一个选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param value
	 * @throws Exception
	 */
	void selectByValue(String xpath, String value);

	/**
	 * Select an option by value from &lt;select&gt; web element.
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到下拉菜单，并根据值（value），选择其中一个选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 * @param value
	 * @throws Exception
	 */
	void selectByValue(String[] xpathArray, String value);
	
	/**
	 * deSelect an option by visible text from &lt;select&gt; web element.
	 * 根据Xpath找到下拉菜单，并根据展示的文字（VisibleText），选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param option
	 * @throws Exception
	 */
	void deSelect(String xpath, String option);
	
	/**
	 * deSelect an option by visible text from &lt;select&gt; web element.
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到下拉菜单，并根据展示的文字（VisibleText），选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param option
	 * @throws Exception
	 */
	void deSelect(String[] xpathArray, String option);
	
	/**
	 * deSelect an option by index from &lt;select&gt; web element.
	 * 根据Xpath找到下拉菜单，并根序号，选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param index
	 * @throws Exception
	 */
	void deSelectByIndex(String xpath, int index);

	/**
	 * deSelect an option by index from &lt;select&gt; web element.
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到下拉菜单，并根据序号，选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 * @param index
	 * @throws Exception
	 */
	void deSelectByIndex(String[] xpathArray, int index);
	
	/**
	 * deSelect an option by value from &lt;select&gt; web element.
	 * 根据Xpath找到下拉菜单，并根据值（value），选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpath
	 * @param value
	 * @throws Exception
	 */
	void deSelectByValue(String xpath, String value);
	
	/**
	 * deSelect an option by value from &lt;select&gt; web element.
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率）找到下拉菜单，并根据值（value），选择除该选项之外的所有选项
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 * @param xpathArray
	 * @param value
	 * @throws Exception
	 */
	void deSelectByValue(String[] xpathArray, String value);
	
	/**
	 * 根据Xpath清除下拉菜单为初始状态
	 * 如果是多选的，则全部不选
	 * 如果是单选的，则选中序号为0的
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
	void clearSelection(String xpath);
	
	/**
	 * 根据多个xpath（同一个控件的不同Xpath写法，增大找到控件的几率），清除下拉菜单为初始状态
	 * 如果是多选的，则全部不选
	 * 如果是单选的，则选中序号为0的
	 * 包含重试和超时机制
	 * 支持设置预先等待时间，以方便肉眼能跟上（通过设置pause变量）
	 */
	void clearSelection(String[] xpathArray);
	
	/**
	 * 根据Xpath获取下拉菜单的所有可选项
	 * 无重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * 返回MAP：
	 * 			"value", webElement.getAttribute("value")
	 *			"text", webElement.getText()
	 */
	List<Map<String, String>> getAllOptions(String xpath);
	
	/**
	 * 根据Xpath获取下拉菜单的所有已选择的选项
	 * 无重试和超时机制
	 * 不支持设置预先等待时间（通过设置pause变量）
	 * 返回MAP：
	 * 			"value", webElement.getAttribute("value")
	 *			"text", webElement.getText()
	 */
	List<Map<String, String>> getAllSelectedOptions(String xpath);

	/**
	 *
	 * @Title: uploadFile
	 * @Description: 文件上传,注意filePath必须是绝对路径
	 * @param : filePath 文件路径
	 * @param : xpathArray  控件的xpath
	 * @return: void
	 */
	void uploadFile(String filePath, String[] xpathArray);

	/**
	 * 点击浏览器原生的alert
	 */
	void clickAlert();
	
	/**
	 * 根据Xpath，找到控件并返回
	 * 有重试和超时机制
	 */
	WebElement findElementByXpath(String xpath);
	
	/**
	 * 获取当前URL
	 */
	String getCurrentUrl();
}