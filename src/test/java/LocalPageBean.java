import lazy.test.ui.annotations.*;
import lazy.test.ui.beans.PageBean;
import lazy.test.ui.browser.BrowserEmulator;
import lazy.test.ui.controls.*;

public class LocalPageBean extends PageBean {

    private String test = "test";

    @Xpath(xpath={"//input[@id='username']","/html/body/p[1]/input"})
    @Frame(frame="")
    @Description(description="username")
    Text username;

    @Xpath(xpath={"//input[@id='date']","/html/body/p[3]/input"})
    @Frame(frame="")
    @Description(description="date")
    Calendar date;

    @Xpath(xpath={"//input[@id='hehe']","//input[contains(@value,'hehe')]","/html/body/p[4]/input"})
    @Frame(frame="")
    @Description(description="hehe")
    Click hehe;

    @Xpath(xpath={"//select[@id='type']","/html/body/p[2]/select"})
    @Frame(frame="")
    @Description(description="type")
    Select type;

    @Xpath(xpath={"//a[@id='keke']","//a[contains(text(),'keke')]","/html/body/p[5]/a"})
    @Frame(frame="")
    @Description(description="keke")
    Click keke;

    @Xpath(xpath={"//input[@id='loginTypeNormal']","/html/body/p[1]/input[1]"})
    @Frame(frame="addOpt")
    @Description(description="loginTypeNormal")
    Check loginTypeNormal;

    @Xpath(xpath={"//input[@id='loginTypeSec']","//input[contains(@value,'sec')]","/html/body/p[1]/input[2]"})
    @Frame(frame="addOpt")
    @Description(description="loginTypeSec")
    Check loginTypeSec;

    @Xpath(xpath={"//input[@id='loginTypePwd']","/html/body/p[2]/input[1]"})
    @Frame(frame="addOpt")
    @Description(description="loginTypePwd")
    Check loginTypePwd;

    @Xpath(xpath={"//input[@id='loginTypeDym']","/html/body/p[2]/input[2]"})
    @Frame(frame="addOpt")
    @Description(description="loginTypeDym")
    Check loginTypeDym;

    @TextContent(textContent={"类型","日期"})
    @Frame(frame="")
    @Description(description="textContents")
    PlainText textContents;

    @TextContent(textContent={"类型","选项"})
    @Frame(frame="addOpt")
    @Description(description="textContentIframe")
    PlainText textContentIframe;

    @Xpath(xpath={"//input[@id='file']", "/html/body/p[6]/input"})
    @Frame(frame="")
    @Description(description="file")
    public FileInput file;

    @Xpath(xpath={"//table[@id='table']", "//table[contains(@style,'border: 1px solid;')]", "/html/body/table"})
    @Frame(frame="")
    @Description(description="table")
    public Table table;

    @Xpath(xpath={"//table[@id='table']", "//table[contains(@style,'border: 1px solid;')]", "/html/body/table"})
    @Frame(frame="addOpt")
    @Description(description="tableAddOpt")
    public Table tableAddOpt;

    public LocalPageBean(BrowserEmulator be) { super(be); }

}