package com.vidoc.command.interaction;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Clears the content of an input field identified by a CSS selector.
 * Corresponds to: clear "#selector"
 */
public class ClearCommand implements Command {

    private final String selector;

    public ClearCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Locates the element identified by the CSS selector and clears its content.
     * <p>
     * Only input and textarea elements are valid targets for this command.
     * If the selector resolves to any other element type, an
     * {@link IllegalArgumentException} is thrown with a descriptive message
     * indicating what tag was found, so the script author can identify and
     * fix the incorrect selector immediately.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws IllegalArgumentException if the target element is not an input or textarea
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        WebElement element = driver.findElement(By.cssSelector(this.selector));
        String tagName = element.getTagName().toLowerCase();
        if (!tagName.equals("input") && !tagName.equals("textarea")) {
            throw new IllegalArgumentException(
                    "clear command can only be used on input or textarea elements, but got: <" + tagName + ">"
            );
        }
        element.clear();
    }
}