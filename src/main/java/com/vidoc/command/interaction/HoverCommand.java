package com.vidoc.command.interaction;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Moves the mouse over a web element identified by a CSS selector.
 * Corresponds to: hover "#selector"
 */
public class HoverCommand implements Command {

    private final String selector;

    public HoverCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Locates the element identified by the CSS selector and moves the mouse over it.
     * <p>
     * The element is checked for visibility, interactability and size before the
     * hover action is performed. If any of these checks fail, an
     * {@link IllegalArgumentException} is thrown with a descriptive message so the
     * script author can identify and fix the incorrect selector immediately.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws IllegalArgumentException if the target element is not visible, not enabled,
     *                                  or has zero dimensions
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        WebElement element = driver.findElement(By.cssSelector(this.selector));
        if (!element.isDisplayed()) {
            throw new IllegalArgumentException(
                    "hover command target is not visible: " + this.selector
            );
        }
        if (!element.isEnabled()) {
            throw new IllegalArgumentException(
                    "hover command target is not enabled: " + this.selector
            );
        }
        if (element.getSize().getWidth() == 0 || element.getSize().getHeight() == 0) {
            throw new IllegalArgumentException(
                    "hover command target has no size — element may be hidden: " + this.selector
            );
        }
        new Actions(driver).moveToElement(element).perform();
    }
}