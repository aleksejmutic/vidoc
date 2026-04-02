package com.vidoc.command.documentation;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;

/**
 * Takes a screenshot of either the full page or a specific element.
 * Corresponds to: screenshot or screenshot "#selector"
 */
public class ScreenshotCommand extends BaseCommand {

    private final String selector;

    public ScreenshotCommand(String selector) {
        this.selector = selector;
    }

    /**
     * Takes a screenshot and stores the path in the execution context.
     * <p>
     * If a selector is provided the element is scrolled into view first
     * and then an element-level screenshot is taken using Selenium 4's
     * native element screenshot support. If no selector is provided a
     * full page screenshot is taken instead.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        File screenshot;
        if (this.selector != null && !this.selector.isEmpty()) {
            WebElement element = driver.findElement(By.cssSelector(this.selector));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            screenshot = element.getScreenshotAs(OutputType.FILE);
        } else {
            screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        }
        executionContext.setLastScreenshot(screenshot.getAbsolutePath());
    }
}