package com.vidoc.command.documentation;

import com.vidoc.command.BaseCommand;
import com.vidoc.context.ExecutionContext;
import com.vidoc.context.ScreenshotMode;
import org.openqa.selenium.WebDriver;

/**
 * Sets the screenshot capture mode for all subsequent commands in the script.
 * <p>
 * This command corresponds to the {@code screenshotmode} directive in a .visc script:
 * <pre>
 *   screenshotmode "all"     # auto-screenshot every command
 *   screenshotmode "manual"  # only explicit screenshot commands capture
 * </pre>
 * This command is invisible in the output — it produces no slide of its own.
 * The engine skips adding a StepResult for it. It only mutates the context.
 */
public class ScreenshotModeCommand extends BaseCommand {

    private final ScreenshotMode mode;

    public ScreenshotModeCommand(ScreenshotMode mode) {
        this.mode = mode;
    }

    public ScreenshotMode getMode() {
        return mode;
    }

    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        executionContext.setScreenshotMode(mode);
    }
}