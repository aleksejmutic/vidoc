package com.vidoc.command.assertion;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Asserts that a web element is visible on the page.
 * Corresponds to: assertVisible "#selector"
 */
public class AssertVisibleCommand implements Command {

    private final String selector;

    public AssertVisibleCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Locates the element and checks that it is displayed on the page.
     * If the element is not visible, an {@link AssertionError} is thrown.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws AssertionError if the element is not visible
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        if (!driver.findElement(By.cssSelector(this.selector)).isDisplayed()) {
            throw new AssertionError(
                    "assertVisible failed — element is not visible: " + this.selector
            );
        }
    }
}