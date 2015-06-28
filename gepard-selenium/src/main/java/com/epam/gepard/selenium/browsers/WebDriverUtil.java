package com.epam.gepard.selenium.browsers;

/*==========================================================================
 Copyright 2004-2015 EPAM Systems

 This file is part of Gepard.

 Gepard is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gepard is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import com.epam.gepard.exception.SimpleGepardException;
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.selenium.helper.EnvironmentHelper;
import com.epam.gepard.util.FileUtil;
import com.epam.gepard.util.Util;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility functions for Selenium.
 * @author Tamas_Kohegyi
 */

public class WebDriverUtil {

    public static final String SELENIUM_PORT = "gepard.selenium.port";
    public static final String SELENIUM_HOST = "gepard.selenium.host";

    public static final String DEFAULT_TIMEOUT = "30000";

    public static final String SELENIUM_BROWSER_FIREFOX = "gepard.selenium.browserString.FF";
    public static final String SELENIUM_BROWSER_INTERNET_EXPLORER = "gepard.selenium.browserString.IE";
    public static final String SELENIUM_BROWSER_GOOGLE_CHROME = "gepard.selenium.browserString.GoogleChrome";
    public static final String SELENIUM_BROWSER_SAFARI = "gepard.selenium.browserString.Safari";
    private static int dumpFileCount;
    private GepardTestClass tc;

    /**
     * WebDriver main object.
     */
    private WebDriver webDriver;
    /**
     * Selenium main object.
     */
    private Selenium selenium;


    /**
     * Environment extender.
     */
    private EnvironmentHelper environmentHelper;

    private String browserString;

    public WebDriverUtil(final GepardTestClass clazz) {
        tc = clazz;
        environmentHelper = new EnvironmentHelper(tc.getTestClassExecutionData().getEnvironment());
        setBrowserString(environmentHelper.getTestEnvironmentBrowser());
    }


    public WebDriver buildWebDriverInstance(final String baseUrl) {
        if (browserString == null) {
            throw new SimpleGepardException("No browser to be used was specified.");
        }


        DesiredCapabilities capabilities = detectCapabilities();

        try {
            webDriver = new RemoteWebDriver(buildWebDriverUrl(), capabilities);
            selenium = new WebDriverBackedSelenium(webDriver, baseUrl);
            selenium.setTimeout(DEFAULT_TIMEOUT); //set the default timeout
        } catch (WebDriverException | SeleniumException e) {
            throw new SimpleGepardException(e.getLocalizedMessage());
        }
        // hide all open windows on Mac, it is necessary to hide 'always on top' windows, otherwise useless screenshots will be created from desktop
        if (getBrowserType() == BrowserEnum.Safari) {
            selenium.keyPressNative(String.valueOf(KeyEvent.VK_F11));
        }

        return webDriver;
    }

    public void destroyWebDriverInstance() {
        if (webDriver != null) {
            webDriver.close();
            try {
                Thread.sleep(5000);
                webDriver.quit(); //close all opened browser window
            } catch(Exception e) {
            }
            webDriver = null;
        }
        selenium = null;
    }

    private URL buildWebDriverUrl() {
        URL retVal;

        try {
            retVal = new URIBuilder().setScheme(getWebDriverScheme()).setHost(getSeleniumHost()).setPort(getSeleniumPort())
                    .setPath(getWebDriverPath()).build().toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Unable to build RemoteWebDriver's url!");
        }

        return retVal;
    }

    private String getWebDriverScheme() {
        return "http";
    }

    private String getSeleniumHost() {
        return environmentHelper.getProperty(SELENIUM_HOST, "localhost");
    }

    private int getSeleniumPort() {
        return Integer.valueOf(environmentHelper.getProperty(SELENIUM_PORT, "4444"));
    }

    private String getWebDriverPath() {
        return "/wd/hub";
    }

    private DesiredCapabilities detectCapabilities() {
        DesiredCapabilities capabilities = null;
        try {
            if (browserString.compareTo(environmentHelper.getProperty(SELENIUM_BROWSER_GOOGLE_CHROME)) == 0) {
                capabilities = DesiredCapabilities.chrome();
                capabilities.setBrowserName("chrome");
            }
            if (browserString.compareTo(environmentHelper.getProperty(SELENIUM_BROWSER_FIREFOX)) == 0) {
                capabilities = DesiredCapabilities.firefox();
                capabilities.setBrowserName("firefox");
                capabilities.setVersion("ANY");
            }
            if (browserString.compareTo(environmentHelper.getProperty(SELENIUM_BROWSER_INTERNET_EXPLORER)) == 0) {
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setBrowserName("internetExplorer");
            }
            if (browserString.compareTo(environmentHelper.getProperty(SELENIUM_BROWSER_SAFARI)) == 0) {
                capabilities = DesiredCapabilities.safari();
                capabilities.setBrowserName("safari");
            }
            if (capabilities == null) {
                throw new SimpleGepardException("Specified browser:'" + browserString + "' is not supported.");
            }
        } catch (NullPointerException e) {
            throw new SimpleGepardException("Gepard property values for Selenium Browsers are not available.");
        }
        return capabilities;
    }

    /**
     * Sets the actually used browser.
     *
     * @param browserString is the string that identified the browser.
     * @return with the browser string (like: *chrome).
     */
    public String setBrowserString(String browserString) {
        this.browserString = browserString;
        return this.browserString;
    }

    public Selenium getSelenium() {
        return selenium;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public GepardTestClass getGepardTestClass() { return tc; }
    /**
     * Identify whether the current browser is a firefox instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is a firefox browser.
     */
    public boolean isFireFox(final String browserString) {
        return browserString.endsWith("*chrome") || browserString.endsWith("*firefox") || browserString.endsWith("*firefoxproxy");
    }

    /**
     * Identify whether the current browser is a safari instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is a safari browser.
     */
    public boolean isSafari(final String browserString) {
        return browserString.endsWith("*safari");
    }

    /**
     * Identify whether the current browser is an Internet Explorer instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is an Internet Explorer browser.
     */
    public boolean isInternetExplorer(final String browserString) {
        return browserString.endsWith("*iexplore") || browserString.endsWith("*iehta") || browserString.endsWith("*iexploreproxy");
    }

    /**
     * Identify whether the current browser is a Google Chrome instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is a Google Chrome browser.
     */
    public boolean isGoogleChrome(final String browserString) {
        return browserString.endsWith("*googlechrome");
    }

    /**
     * Detect the actual browser type.
     *
     * @return with the (enumerated) browser type.
     */
    public BrowserEnum getBrowserType() {
        return getBrowserType(browserString);
    }

    /**
     * Detect the actual browser type.
     *
     * @param browserString is the used browser string in the test class
     * @return with the (enumerated) browser type.
     */
    public BrowserEnum getBrowserType(final String browserString) {
        BrowserEnum type = BrowserEnum.Unknown;
        if (isFireFox(browserString)) {
            type = BrowserEnum.FireFox;
        }
        if (isSafari(browserString)) {
            type = BrowserEnum.Safari;
        }
        if (isInternetExplorer(browserString)) {
            type = BrowserEnum.InternetExplorer;
        }
        if (isGoogleChrome(browserString)) {
            type = BrowserEnum.GoogleChrome;
        }
        return type;
    }

    /**
     * Create the BrowserHandlerBase according to the browser type.
     *
     * @param tc The test case class.
     * @return with the BrowserUtil.
     */
    public BrowserHandlerBase getBrowserUtil(final GepardTestClass tc) {
        BrowserEnum browser = getBrowserType();

        BrowserHandlerBase retVal;
        switch (browser) {
        case FireFox:
            retVal = new FirefoxBrowserHandler(tc, this);
            break;
        case InternetExplorer:
            retVal = new InternetExplorerBrowserHandler(tc, this);
            break;
        case Safari:
            retVal = new SafariBrowserHandler(tc, this);
            break;
        case GoogleChrome:
            retVal = new ChromeBrowserHandler(tc, this);
            break;
        default:
            throw new IllegalStateException("Unknown browser type [" + browser + "], handler class could not be created.");
        }

        return retVal;
    }

    /**
     * Opens to a specific URL.
     * Uses timeout of fix 30 seconds.
     *
     * @param url is the URL the browser should go.
     */
    public void gotoUrl(final String url) {
        tc.logComment("Open URL: " + url);
        webDriver.get(url);
        logEvent("Page loaded", true);
    }

    /**
     * Click on a specific button on the page.
     *
     * @param tc is the caller Test Case
     * @param path is the path to the button
     */
    public void clickOnElement(final GepardTestClass tc, final String path) {
        tc.logComment("Click on: " + path);
        WebElement e = webDriver.findElement(By.xpath(path));
        e.click();
        logEvent("Page updated.", true);
    }

    /**
     * Write an event message to the log.
     *
     * @param text     Event message
     * @param makeDump if true, page source dump will be created.
     */
    public void logEvent(final String text, final boolean makeDump) {
        String addStr = "";
        if (!text.startsWith("<font")) {
            tc.getTestClassExecutionData().addSysOut(text);
        }
        if (tc.getTestClassExecutionData().getHtmlRunReporter().getTestMethodHtmlLog() != null) {
            if (makeDump) {
                try {
                    String dumpFileName = dumpSource(true);
                    File dumpFile = new File(dumpFileName);
                    String screenshotFileName = dumpFileName + ".png";
                    if (selenium != null) {
                        selenium.windowMaximize();
                        WebDriver augmentedDriver = new Augmenter().augment(webDriver);
                        File screenShot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
                        FileUtil fileUtil = new FileUtil();
                        File screenShotFile = new File(screenshotFileName);
                        fileUtil.copy(screenShot, screenShotFile);
                    }
                    String dumpFileName2 = dumpSource(false);
                    File dumpFile2 = new File(dumpFileName2);
                    addStr = " <small>[<a href=\"" + dumpFile.getName() + "\" target=\"_new\">source</a>]" + " [<a href=\"" + dumpFile2.getName()
                            + "\" target=\"_new\">view</a>]" + " [<a href=\"" + dumpFile.getName() + ".png\" target=\"_new\">screenshot</a>]</small>";
                } catch (Exception e) {
                    addStr = " <small>[Dump failed]</small>";
                }
            }
            tc.getTestClassExecutionData().getHtmlRunReporter().getTestMethodHtmlLog().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0F0\">" + text + addStr + "</td></tr>\n");
        }
    }

    /**
     * Write current HTML response data to a file in the current test case's log directory.
     *
     * @param escapeHTML HTML output will be escaped if true
     * @return Path of the created file
     * @throws FileNotFoundException when problem occurred.
     */
    public String dumpSource(boolean escapeHTML) throws FileNotFoundException {
        String newFilePath;
        String logPath = tc.getTestClassExecutionData().getHtmlRunReporter().getTestMethodHtmlLog().getLogPath();
        String logPathCanonical = logPath.replace('\\', '/');
        int pos = logPathCanonical.lastIndexOf('/');
        int fileCount = dumpFileCount++;

        if (pos == -1) {
            newFilePath = "dump" + Integer.toString(fileCount) + ".html";
        } else {
            newFilePath = logPath.substring(0, pos) + "/dump" + Integer.toString(fileCount) + ".html";
        }

        dumpSource(newFilePath, escapeHTML);

        return newFilePath;
    }

    /**
     * Write current HTML response data to file. This is not the XML representation, therefore
     * may be used only after explicit page load.
     *
     * @param fileName   Target file path
     * @param escapeHTML HTML output will be escaped if true
     * @throws FileNotFoundException when file is not available.
     */
    public void dumpSource(final String fileName, final boolean escapeHTML) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream(fileName));
        out.println("<!-- Dumped on " + (new SimpleDateFormat()).format(new Date()) + ", URL: " + selenium.getLocation() + " -->");

        String source = selenium.getHtmlSource();

        if (escapeHTML) {
            Util util = new Util();
            source = util.escapeHTML(source);
            out.println("<html><body><pre>");
            out.println(source);
            out.println("</pre></body></html>");
        } else {
            out.print(source);
        }
        out.flush();
    }

}
