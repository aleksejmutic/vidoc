package com.vidoc.command.keyboard;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * Simulates pressing a single keyboard key.
 * Corresponds to: press "Enter"
 */
public class PressCommand extends BaseCommand {

    private final String key;

    public PressCommand(String key) {
        this.key = key;
    }

    /**
     * Sends a single key press to the browser using the Actions API.
     * The key string is matched against Selenium's Keys enum — for example
     * "Enter", "Tab", "Escape", "ArrowUp" etc.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        Keys seleniumKey = Keys.valueOf(this.key.toUpperCase());
        new Actions(driver).sendKeys(seleniumKey).perform();
    }
}