package com.vidoc.context;

/**
 * Represents the result of a single executed step in a Vidoc script.
 * <p>
 * Each step corresponds to one command in the .visc file. A StepResult
 * captures everything that happened during that step — the comment that
 * described it, the name of the command that ran, the path to the screenshot
 * taken after execution, whether the step passed or failed, and the error
 * message if it did fail.
 * <p>
 * StepResult objects are collected inside {@link ExecutionContext} and later
 * used by the documentation generators to build the HTML, PDF, and video output.
 */
public class StepResult {
    private final String comment;        // the # comment above the command
    private final String commandName;    // e.g. "click", "type"
    private final String screenshotPath; // path to the screenshot file
    private final boolean success;       // did it pass or fail
    private final String errorMessage;   // if it failed, why

    public StepResult(String comment, String commandName, String screenshotPath, boolean success, String errorMessage) {
        this.comment = comment;
        this.commandName = commandName;
        this.screenshotPath = screenshotPath;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getComment() {
        return comment;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
