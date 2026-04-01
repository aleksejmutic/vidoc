package com.vidoc.engine;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Executes a list of commands produced by the VidocScriptVisitor.
 * <p>
 * The engine iterates through every command in order and calls execute()
 * on each one, passing the WebDriver and ExecutionContext. If any command
 * fails the exception propagates up and execution stops at that step.
 */
public class ExecutionEngine {

    /**
     * Runs all commands sequentially in the order they appear in the script.
     *
     * @param commands         the list of commands to execute
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the shared context that accumulates steps and variables
     */
    public void run(List<Command> commands, WebDriver driver, ExecutionContext executionContext) {
        for (Command command : commands) {
            command.execute(driver, executionContext);
        }
    }
}