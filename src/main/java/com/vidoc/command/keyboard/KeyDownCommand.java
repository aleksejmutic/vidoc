package com.vidoc.command.keyboard;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * Holds a keyboard key down without releasing it.
 * Corresponds to: keyDown "Shift"
 */
public class KeyDownCommand implements Command {

    private final String key;

    public KeyDownCommand(String key) {
        this.key = key;
    }

    /**
     * Presses and holds the specified key using the Actions API.
     * Typically used before a click or another key to simulate combinations
     * like Shift+Click or Ctrl+A.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        Keys seleniumKey = Keys.valueOf(this.key.toUpperCase());
        new Actions(driver).keyDown(seleniumKey).perform();
    }
}