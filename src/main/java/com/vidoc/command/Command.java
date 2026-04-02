package com.vidoc.command;

import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

/**
 * Represents a single executable instruction from a .visc script.
 * <p>
 * Each command carries its own comment — the {@code #} line that appeared
 * directly above it in the script. The comment is set by the visitor at
 * parse time via {@link #setComment(String)} and read by the engine at
 * execution time to attach to the resulting {@link com.vidoc.context.StepResult}.
 */
public interface Command {

    /**
     * Executes this command using the given WebDriver and context.
     *
     * @param driver           the Selenium WebDriver controlling the browser
     * @param executionContext the shared execution context
     */
    void execute(WebDriver driver, ExecutionContext executionContext);

    /**
     * Sets the comment that was written above this command in the .visc script.
     * Called by the visitor immediately after the command is created.
     *
     * @param comment the comment text, or null if no comment preceded this command
     */
    void setComment(String comment);

    /**
     * Returns the comment that was written above this command in the .visc script.
     * Used by the engine when building the StepResult for this command.
     *
     * @return the comment text, or null if none
     */
    String getComment();
}