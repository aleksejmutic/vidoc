package com.vidoc.command.assertion;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Asserts that a web element contains the expected text.
 * Corresponds to: assert "#selector" : "expected text"
 */
public class AssertCommand implements Command {

    private final String selector;
    private final String expectedText;

    public AssertCommand(String selector, String expectedText) {
        this.selector = selector;
        this.expectedText = expectedText;
    }

    /**
     * Locates the element and checks that its text content matches the expected value.
     * If the text does not match, an {@link AssertionError} is thrown with a descriptive
     * message showing both the expected and actual text so the script author can
     * immediately see what went wrong.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws AssertionError if the element's text does not match the expected value
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        WebElement element = driver.findElement(By.cssSelector(this.selector));
        String actualText = element.getText();
        if (!actualText.equals(this.expectedText)) {
            throw new AssertionError(
                    "assert failed for selector: " + this.selector +
                            "\n  expected: " + this.expectedText +
                            "\n  actual:   " + actualText
            );
        }
    }
}