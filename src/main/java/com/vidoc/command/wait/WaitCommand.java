package com.vidoc.command.wait;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Waits until a web element identified by a CSS selector is present in the DOM.
 * Corresponds to: wait "#selector"
 */
public class WaitCommand extends BaseCommand {

    private final String selector;

    public WaitCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Waits up to 10 seconds for the element identified by the CSS selector
     * to become present in the DOM. If the element does not appear within
     * the timeout a TimeoutException is thrown by Selenium.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(this.selector)
                ));
    }
}