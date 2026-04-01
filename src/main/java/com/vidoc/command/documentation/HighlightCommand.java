package com.vidoc.command.documentation;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Highlights a web element by drawing a visible border around it.
 * Used for documentation purposes to draw attention to elements in screenshots.
 * Corresponds to: highlight "#selector"
 */
public class HighlightCommand implements Command {

    private final String selector;

    public HighlightCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Finds the element and applies a bright red border around it using JavaScript.
     * This is purely visual — it does not interact with the element or affect
     * the page's functionality. The highlight appears in subsequent screenshots
     * taken after this command runs.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        WebElement element = driver.findElement(By.cssSelector(this.selector));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='3px solid red';", element
        );
    }
}