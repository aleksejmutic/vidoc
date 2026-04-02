package com.vidoc.command.interaction;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Scrolls the page down by the specified number of pixels.
 * Corresponds to: scrollDown 500
 */
public class ScrollDownCommand extends BaseCommand {

    private final int pixels;

    public ScrollDownCommand(int pixels) {
        this.pixels = pixels;
    }

    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + this.pixels + ");");
    }
}