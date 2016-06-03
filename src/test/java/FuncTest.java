import junit.framework.Assert;
import lazy.test.ui.beans.TableSize;
import lazy.test.ui.browser.BrowserEmulator;
import lazy.test.ui.browser.BrowserEmulatorImpl;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by sushidong on 2016/3/31.
 */
public class FuncTest {
    BrowserEmulator be;

    @BeforeClass
    public void setUp() {
        be = new BrowserEmulatorImpl();
    }

    @AfterClass(alwaysRun=true)
    public void tearDown() {
        be.quit();
    }

    @Test
    public void main() throws Exception {
        be.open("file://"+this.getClass().getResource("/html/test.html").getFile());
        LocalPageBean localPageBean = new LocalPageBean(be);

        localPageBean.username.input("mama");

        localPageBean.username.clear();

        localPageBean.type.selectByVisibleText("BB");

        localPageBean.type.clear();

        localPageBean.type.selectByIndex(1);

        Assert.assertEquals(1, localPageBean.type.getAllSelectedOptions().size());
        Assert.assertEquals("B", localPageBean.type.getAllSelectedOptions().get(0).get("value"));
        Assert.assertEquals("BB", localPageBean.type.getAllSelectedOptions().get(0).get("text"));

        localPageBean.type.selectByValue("A");

        Assert.assertEquals(1, localPageBean.type.getAllSelectedOptions().size());
        Assert.assertEquals("A", localPageBean.type.getAllSelectedOptions().get(0).get("value"));
        Assert.assertEquals("AA", localPageBean.type.getAllSelectedOptions().get(0).get("text"));

        localPageBean.type.selectByValue("B");
        localPageBean.type.clear();

        Assert.assertEquals(2, localPageBean.type.getAllOptions().size());
        Assert.assertEquals("A", localPageBean.type.getAllOptions().get(0).get("value"));
        Assert.assertEquals("AA", localPageBean.type.getAllOptions().get(0).get("text"));
        Assert.assertEquals("B", localPageBean.type.getAllOptions().get(1).get("value"));
        Assert.assertEquals("BB", localPageBean.type.getAllOptions().get(1).get("text"));

        localPageBean.date.input("2016-03-31");

        localPageBean.date.clear();

        localPageBean.hehe.click();

        localPageBean.keke.click();

        Assert.assertFalse(localPageBean.loginTypeSec.isChecked());

        localPageBean.loginTypeSec.check();

        Assert.assertTrue(localPageBean.loginTypeSec.isChecked());

        localPageBean.loginTypeNormal.check();

        localPageBean.loginTypeNormal.unCheck();

        Assert.assertFalse(localPageBean.loginTypeNormal.isChecked());

        localPageBean.loginTypePwd.check();

        localPageBean.loginTypeDym.check();

        localPageBean.loginTypePwd.unCheck();

        localPageBean.loginTypeDym.unCheck();

        localPageBean.loginTypeDym.expectElementExists();

        Assert.assertTrue(localPageBean.textContents.isExists());

        Assert.assertTrue(localPageBean.textContentIframe.isExists());

        Assert.assertEquals("keke", localPageBean.username.getText());

        localPageBean.file.uploadFile("C:\\Windows\\System32\\drivers\\etc\\hosts");

        Assert.assertEquals("5", localPageBean.table.getTableContents().get(1).get(1));

        Assert.assertEquals("2", localPageBean.table.getTableCellText(1, 2));

        TableSize tableSize = localPageBean.table.getTableSize();

        Assert.assertEquals(2, tableSize.getRowCount());
        Assert.assertEquals(3, tableSize.getColCount());

        Assert.assertEquals("2", localPageBean.tableAddOpt.getTableContents().get(1).get(0));

        Assert.assertEquals("4", localPageBean.tableAddOpt.getTableCellText(3, 2));

        tableSize = localPageBean.tableAddOpt.getTableSize();

        Assert.assertEquals(3, tableSize.getRowCount());
        Assert.assertEquals(2, tableSize.getColCount());
    }
}
