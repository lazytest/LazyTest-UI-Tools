package lazy.test.ui.browser;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log Tools
 */
public class LogTools {

    private static final Logger logger = LoggerFactory.getLogger(LogTools.class);

	public static void log(String logText) {
		//System.out.println("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + "] " + logText);
        logger.info(logText);
	}

	public static String screenShot(BrowseEmulator be) {
		String dir = "screenshot"; // TODO
		String time = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
		String screenShotPath = dir + File.separator + time + ".png";

		WebDriver augmentedDriver = null;
		if (GlobalSettings.browserCoreType == 1 || GlobalSettings.browserCoreType == 3) {
			augmentedDriver = be.getBrowserCore();
			augmentedDriver.manage().window().setPosition(new Point(0, 0));
			augmentedDriver.manage().window().setSize(new Dimension(9999, 9999));
		} else if (GlobalSettings.browserCoreType == 2) {
			augmentedDriver = new Augmenter().augment(be.getBrowserCore());
		} else {
			return "Incorrect browser type";
		}

		try {
			File sourceFile = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(sourceFile, new File(screenShotPath));
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to screenshot";
		}

		// Convert '\' into '/' for web image browsing.
		return screenShotPath.replace("\\", "/");
	}

}
