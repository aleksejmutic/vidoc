package com.vidoc.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all shared states during the execution of a Vidoc script.
 * <p>
 * ExecutionContext is created once at the start of a script run and passed
 * to every command as it executes. It serves three purposes:
 * <ul>
 *   <li>Accumulates {@link StepResult} objects as each command finishes,
 *       which the generators later use to build documentation.</li>
 *   <li>Stores variables declared with the {@code set} command so they
 *       can be resolved when referenced later in the script with a $ prefix.</li>
 *   <li>Carries the pending comment — the last # comment seen in the script —
 *       so it can be attached to the next command that executes.</li>
 * </ul>
 */
public class ExecutionContext {

    private final List<StepResult> steps;
    private final Map<String, String> variables;
    private String pendingComment;          //the comment is pending until we stumble upon the command directly below it, the command the comment refers to
    private String lastScreenshot;
    private ScreenshotMode screenshotMode;

    public ExecutionContext() {
        this.steps = new ArrayList<>();
        this.variables = new HashMap<>();
        this.pendingComment = null;
        this.lastScreenshot = null;
        this.screenshotMode = ScreenshotMode.ALL;
    }

    public void addStep(StepResult step) {
        this.steps.add(step);
    }

    public void setVariable(String name, String value) {
        this.variables.put(name, value);
    }

    public String getVariable(String name) {
        return this.variables.get(name);
    }

    public void setPendingComment(String comment) {
        this.pendingComment = comment;
    }

    public String getLastScreenshot() {
        return lastScreenshot;
    }

    public void setLastScreenshot(String lastScreenshot) {
        this.lastScreenshot = lastScreenshot;
    }

    public ScreenshotMode getScreenshotMode() {
        return screenshotMode;
    }

    public void setScreenshotMode(ScreenshotMode screenshotMode) {
        this.screenshotMode = screenshotMode;
    }

    public String consumePendingComment() {
        String comment = this.pendingComment;
        this.pendingComment = null;
        return comment;
    }

    public List<StepResult> getSteps() {
        return this.steps;
    }
}
