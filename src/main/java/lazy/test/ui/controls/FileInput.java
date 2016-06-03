package lazy.test.ui.controls;

import lazy.test.ui.interfaces.FileInputable;

/**
 * Created by sushidong on 2016/4/26.
 */
public class FileInput extends AbstractControl implements FileInputable {
	/**
	 * 进入iframe，找到控件
	 * 根据本地路径，上传文件
	 * 注意filePath必须是绝对路径
	 */
    public void uploadFile(String filePath) {
    	enterFrame();
        be.uploadFile(filePath, getXpath());
    }
}
