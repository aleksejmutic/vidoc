package com.vidoc.engine;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import com.vidoc.context.StepResult;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Executes a list of commands produced by the VidocScriptVisitor.
 * <p>
 * The engine iterates through every command in order and calls execute()
 * on each one, passing the WebDriver and ExecutionContext. After each
 * command completes — whether successfully or with an error — a
 * {@link StepResult} is created and added to the context so the
 * documentation generators have a full record of what happened.
 * <p>
 * If a command throws an exception the engine catches it, marks the step
 * as failed, records the error message, and continues to the next command.
 * This means a single failing step does not abort the entire script —
 * all steps are always attempted and the failure is visible in the output.
 */
public class ExecutionEngine {

    /**
     * Runs all commands sequentially in the order they appear in the script.
     * <p>
     * Before each command executes the pending comment is consumed from the
     * context — this is the # comment that appeared above the command in the
     * .visc file. The comment is stored in the StepResult so it appears as
     * the step description in the generated HTML and PDF output.
     * <p>
     * After each command executes a StepResult is built from:
     * <ul>
     *   <li>The pending comment consumed before execution</li>
     *   <li>The simple class name of the command (e.g. ClickCommand)</li>
     *   <li>The path of the last screenshot stored in the context</li>
     *   <li>Whether the command succeeded or threw an exception</li>
     *   <li>The exception message if the command failed</li>
     * </ul>
     *
     * @param commands         the ordered list of commands to execute
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext the shared context that accumulates steps and variables
     */
    public void run(List<Command> commands, WebDriver driver, ExecutionContext executionContext) {
        for (Command command : commands) {
            String comment = executionContext.consumePendingComment();
            boolean success = true;
            String errorMessage = null;
            try {
                command.execute(driver, executionContext);
            } catch (Exception e) {
                success = false;
                errorMessage = e.getMessage();
            }
            StepResult step = new StepResult(
                    comment,
                    command.getClass().getSimpleName(),
                    executionContext.getLastScreenshot(),
                    success,
                    errorMessage
            );
            executionContext.addStep(step);
        }
    }
}