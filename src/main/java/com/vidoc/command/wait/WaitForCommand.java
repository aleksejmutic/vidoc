package com.vidoc.command.wait;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

/**
 * Pauses execution for a fixed number of seconds.
 * Corresponds to: waitFor 2
 */
public class WaitForCommand implements Command {

    private final int seconds;

    public WaitForCommand(int seconds) {
        this.seconds = seconds;
    }

    /**
     * Pauses the current thread for the specified number of seconds.
     * Use this for fixed delays such as waiting for animations to complete.
     * Prefer the wait command over this where possible since wait is smarter
     * and only waits as long as necessary.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        try {
            Thread.sleep(this.seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}