package com.vidoc.context;

/**
 * Controls how screenshots are captured during script execution.
 * <p>
 * Set via the {@code screenshotmode} directive in a .visc script:
 * <pre>
 *   screenshotmode "all"     # auto-screenshot every command (default)
 *   screenshotmode "manual"  # only explicit screenshot commands capture
 * </pre>
 */
public enum ScreenshotMode {

    /**
     * The engine automatically takes a screenshot after every command.
     * Explicit {@code screenshot} commands are treated as no-ops.
     * This is the default mode when no {@code screenshotmode} directive is present.
     */
    ALL,

    /**
     * Only explicit {@code screenshot} commands in the script capture anything.
     * All other commands produce a slide with no screenshot.
     */
    MANUAL
}