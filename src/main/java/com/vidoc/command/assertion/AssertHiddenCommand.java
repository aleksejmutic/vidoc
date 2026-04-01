package com.vidoc.command.assertion;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Asserts that a web element is hidden on the page.
 * Corresponds to: assertHidden "#selector"
 */
public class AssertHiddenCommand implements Command {

    private final String selector;

    public AssertHiddenCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Locates the element and checks that it is not displayed on the page.
     * If the element is visible, an {@link AssertionError} is thrown.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws AssertionError if the element is visible when it should be hidden
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        if (driver.findElement(By.cssSelector(this.selector)).isDisplayed()) {
            throw new AssertionError(
                    "assertHidden failed — element is visible when it should be hidden: " + this.selector
            );
        }
    }
}