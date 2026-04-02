package com.vidoc.command.interaction;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Scrolls the browser until the element identified by the CSS selector is in view.
 * Corresponds to: scrollTo "#selector"
 */
public class ScrollToCommand extends BaseCommand {

    private final String selector;

    public ScrollToCommand(String selector) {
        this.selector = selector;
    }

    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        WebElement element = driver.findElement(By.cssSelector(this.selector));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}