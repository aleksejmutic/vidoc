package com.vidoc.command.navigation;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

/**
 * Navigates the browser to the specified URL.
 * Corresponds to: goto "https://myapp.com"
 */
public class GotoCommand extends BaseCommand {

    private final String url;

    public GotoCommand(String url) {
        this.url = url;
    }

    /**
     * Instructs the browser to navigate to the URL provided in the script.
     * <p>
     * Selenium's {@code driver.get()} blocks until the page has fully loaded
     * before returning, so the next command in the script will not execute
     * until the navigation is complete.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        driver.get(this.url);
    }
}