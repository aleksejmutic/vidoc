package com.vidoc.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

/**
 * Factory class responsible for creating a configured WebDriver instance
 * for the specified browser type.
 * <p>
 * Supported browsers are defined in {@link BrowserType}. Each browser
 * is launched using its corresponding Selenium driver. Brave is a special
 * case since it is Chromium-based and requires locating the binary manually
 * before launching via ChromeDriver.
 */
public class BrowserFactory {

    /**
     * Creates and returns a WebDriver instance for the given browser type.
     * <p>
     * Chrome and Firefox are launched using their standard Selenium drivers
     * with no additional configuration. Safari uses the SafariDriver built
     * into macOS. Brave requires additional binary detection — see
     * {@link #createBrave()}.
     *
     * @param type the browser to launch
     * @return a configured and running WebDriver instance
     * @throws IllegalArgumentException if the browser type is not supported
     * @throws IllegalStateException    if Brave is selected but cannot be found on the system
     */
    public static WebDriver create(BrowserType type) {
        switch (type) {
            case CHROME:  return new ChromeDriver();
            case FIREFOX: return new FirefoxDriver();
            case BRAVE:   return createBrave();
            case SAFARI:  return new SafariDriver();
            default: throw new IllegalArgumentException("Unsupported browser: " + type);
        }
    }

    /**
     * Creates a ChromeDriver configured to run the Brave browser.
     * <p>
     * Brave is Chromium-based so it is fully compatible with ChromeDriver,
     * but Selenium cannot detect it automatically — the path to the Brave
     * binary must be set explicitly via {@link ChromeOptions#setBinary(String)}.
     * <p>
     * This method delegates binary detection to {@link #findBraveBinary()} which
     * searches known installation paths for the current operating system. If no
     * valid Brave binary is found an {@link IllegalStateException} is thrown with
     * a clear message guiding the user to either install Brave or switch browsers.
     *
     * @return a ChromeDriver instance pointed at the Brave binary
     * @throws IllegalStateException if the Brave binary cannot be found on the system
     */
    private static WebDriver createBrave() {
        ChromeOptions options = new ChromeOptions();
        String binaryPath = findBraveBinary();
        if (binaryPath == null) {
            throw new IllegalStateException(
                    "Brave browser not found on this system. " +
                            "Please install Brave or use --browser chrome instead."
            );
        }
        options.setBinary(binaryPath);
        return new ChromeDriver(options);
    }

    /**
     * Searches known installation paths for the Brave browser binary
     * on the current operating system.
     * <p>
     * The search order per OS is:
     * <ul>
     *   <li><b>Windows:</b> Program Files installation, then user-level AppData installation</li>
     *   <li><b>macOS:</b> Standard Applications folder</li>
     *   <li><b>Linux:</b> /usr/bin/brave-browser, /usr/bin/brave, /snap/bin/brave,
     *       /usr/bin/brave-browser-stable — covering apt, snap, and other package managers</li>
     * </ul>
     * The first path that exists on the filesystem is returned. If none of the
     * candidate paths exist, null is returned and the caller is responsible for
     * handling the missing binary case.
     *
     * @return the absolute path to the Brave binary, or null if not found
     */
    private static String findBraveBinary() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] candidates;
        if (os.contains("win")) {
            candidates = new String[]{
                    "C:/Program Files/BraveSoftware/Brave-Browser/Application/brave.exe",
                    System.getenv("LOCALAPPDATA") + "/BraveSoftware/Brave-Browser/Application/brave.exe"
            };
        } else if (os.contains("mac")) {
            candidates = new String[]{
                    "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser"
            };
        } else {
            candidates = new String[]{
                    "/usr/bin/brave-browser",
                    "/usr/bin/brave",
                    "/snap/bin/brave",
                    "/usr/bin/brave-browser-stable"
            };
        }
        for (String path : candidates) {
            if (path != null && new java.io.File(path).exists()) {
                return path;
            }
        }
        return null;
    }
}