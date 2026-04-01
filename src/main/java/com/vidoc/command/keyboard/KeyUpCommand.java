package com.vidoc.command.keyboard;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * Releases a keyboard key that was previously held down.
 * Corresponds to: keyUp "Shift"
 */
public class KeyUpCommand implements Command {

    private final String key;

    public KeyUpCommand(String key) {
        this.key = key;
    }

    /**
     * Releases the specified key using the Actions API.
     * Should always be paired with a preceding keyDown command
     * to avoid leaving keys in a held state.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        Keys seleniumKey = Keys.valueOf(this.key.toUpperCase());
        new Actions(driver).keyUp(seleniumKey).perform();
    }
}