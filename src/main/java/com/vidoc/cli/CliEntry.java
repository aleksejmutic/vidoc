package com.vidoc.cli;

import com.vidoc.browser.BrowserType;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Entry point for the Vidoc command line interface.
 * Defines all available commands, options and flags
 * that can be passed when running a .visc script.
 * <p>
 * Example usage:
 * <pre>
 *   vidoc run login.visc --html
 *   vidoc run login.visc --browser brave --all
 *   vidoc run login.visc --pdf --video
 * </pre>
 */
@Command(
        name = "vidoc",
        mixinStandardHelpOptions = true,
        version = "vidoc 1.0.0",
        description = "Runs a .visc script and generates documentation from it."
)
public class CliEntry implements Runnable {

    @Parameters(index = "0", description = "Path to the .visc script to run")
    private String scriptPath;

    @Option(names = "--browser", description = "Browser to use: CHROME, FIREFOX, BRAVE, SAFARI. Default: CHROME", defaultValue = "CHROME")
    private BrowserType browser;

    @Option(names = "--html", description = "Generate an HTML presentation")
    private boolean html;

    @Option(names = "--pdf", description = "Generate a PDF report")
    private boolean pdf;

    @Option(names = "--video", description = "Generate a video recording")
    private boolean video;

    @Option(names = "--all", description = "Generate all output formats")
    private boolean all;

    @Override
    public void run() {
        // wired in Main.java
    }

    public String getScriptPath() { return scriptPath; }
    public BrowserType getBrowser() { return browser; }
    public boolean isHtml() { return html || all; }
    public boolean isPdf() { return pdf || all; }
    public boolean isVideo() { return video || all; }
}