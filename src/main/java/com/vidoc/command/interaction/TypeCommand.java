package com.vidoc.command.interaction;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Types a value into an input field identified by a CSS selector.
 * The value can be a hardcoded string or a variable resolved from the context.
 * Corresponds to: type "#selector" : "value" or type "#selector" : $variable
 */
public class TypeCommand implements Command {

    private final String selector;
    private final String value;

    public TypeCommand(String selector, String value) {
        this.selector = selector;
        this.value = value;
    }

    /**
     * Locates the element identified by the CSS selector and types the given value into it.
     * <p>
     * If the value starts with a $ prefix it is treated as a variable and resolved
     * from the execution context before typing. If the variable is not defined in
     * the context, null will be passed to sendKeys which will cause a runtime error.
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
                    "type command can only be used on input or textarea elements, but got: <" + tagName + ">"
            );
        }
        String resolved = value.startsWith("$")
                ? executionContext.getVariable(value)
                : value;
        element.sendKeys(resolved);
    }
}