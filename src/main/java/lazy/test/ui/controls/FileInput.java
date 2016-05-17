package lazy.test.ui.controls;

import lazy.test.ui.interfaces.FileInputable;

/**
 * Created by sushidong on 2016/4/26.
 */
public class FileInput extends AbstractControl implements FileInputable {
    public void uploadFile(String filePath) {
        be.uploadFile(filePath, getXpath());
    }
}
