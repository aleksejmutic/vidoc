package com.vidoc.command.interaction;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Clicks on a web element identified by a CSS selector.
 * Corresponds to: click "#selector"
 */
public class ClickCommand extends BaseCommand {

    private final String selector;

    public ClickCommand(String selector) {
        this.selector = selector;
    }

    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        var el = driver.findElement(By.cssSelector(this.selector));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", el);
    }
}