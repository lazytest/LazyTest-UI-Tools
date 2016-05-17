package lazy.test.ui.browser;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Global Settings
 * @author ChenKan
 * 需要修改默认值
 */
public class GlobalSettings {

	public static Properties prop = getProperties();

	public static int browserCoreType = Integer.parseInt(prop.getProperty("BrowserCoreType", "2"));

	public static String chromeDriverPath = prop.getProperty("ChromeDriverPath", "/software/chromedriver.exe");

	public static String ieDriverPath = prop.getProperty("IEDriverPath", "/software/IEDriverServer64.exe");

	public static String stepInterval = prop.getProperty("StepInterval", "50");

	public static String timeout = prop.getProperty("Timeout", "2000");
	
	public static String baseStorageUrl = prop.getProperty("baseStorageUrl", System.getProperty("user.dir"));

    public static String pause = prop.getProperty("Pause", "500");

	public static String getProperty(String property) {
		return prop.getProperty(property);
	}
	
	public static Properties getProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream file = new FileInputStream(ClassLoader.getSystemResource("prop.properties").getFile());
			prop.load(file);
			file.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return prop;
	}
}
