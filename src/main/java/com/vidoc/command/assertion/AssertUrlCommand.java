package com.vidoc.command.assertion;

import com.vidoc.command.BaseCommand;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

/**
 * Asserts that the browser is currently on the expected URL.
 * Corresponds to: assertUrl "https://myapp.com/dashboard"
 */
public class AssertUrlCommand extends BaseCommand {

    private final String expectedUrlFragment;

    public AssertUrlCommand(String expectedUrl) {
        this.expectedUrlFragment = expectedUrl;
    }

    /**
     * Checks that the browser's current URL matches the expected URL exactly.
     * If the URLs do not match, an {@link AssertionError} is thrown showing
     * both the expected and actual URL.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws AssertionError if the current URL does not match the expected URL
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        String actualUrl = driver.getCurrentUrl();
        if (!actualUrl.contains(this.expectedUrlFragment)) {
            throw new AssertionError(
                    "assertUrl failed:" +
                            "\n  expected: " + this.expectedUrlFragment +
                            "\n  actual:   " + actualUrl
            );
        }
    }
}