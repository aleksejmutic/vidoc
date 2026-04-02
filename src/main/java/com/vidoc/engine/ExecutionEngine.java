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
 * <p>
 * Comments are read directly from each command object. They were attached
 * to the command at parse time by the visitor, so the comment for each step
 * is always correct regardless of execution order.
 */
public class ExecutionEngine {

    public void run(List<Command> commands, WebDriver driver, ExecutionContext executionContext) {
        for (Command command : commands) {
            // Comment was baked into the command at parse time by VidocScriptVisitor
            String comment = command.getComment();

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