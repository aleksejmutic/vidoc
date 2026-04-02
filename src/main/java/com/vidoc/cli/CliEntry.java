package com.vidoc.cli;

import com.vidoc.browser.BrowserFactory;
import com.vidoc.browser.BrowserType;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import com.vidoc.engine.ExecutionEngine;
import com.vidoc.generator.HtmlGenerator;
import com.vidoc.generator.HtmlTheme;
import com.vidoc.generator.PdfGenerator;
import com.vidoc.generator.VideoFormat;
import com.vidoc.generator.VideoGenerator;
import com.vidoc.parser.VidocScriptParser;
import org.openqa.selenium.WebDriver;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.List;

/**
 * Entry point for the Vidoc command line interface.
 * <p>
 * Example usage:
 * <pre>
 *   vidoc login.visc --report
 *   vidoc login.visc --present
 *   vidoc login.visc --browser BRAVE --report --pdf --video
 *   vidoc login.visc --all
 *   vidoc login.visc --report --theme EARTHY --output ./docs
 * </pre>
 * <p>
 * HTML output modes:
 * <ul>
 *   <li>{@code --report} — command name as title, comment below, pass/fail badge</li>
 *   <li>{@code --present} — comment only beside the screenshot, no status indicators</li>
 * </ul>
 * Both flags can be used together; they will produce separate output files
 * named {@code report.html} and {@code presentation.html} respectively.
 */
@picocli.CommandLine.Command(
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

    /**
     * Generates a report-style HTML file.
     * Each slide shows the command name as a title, the script comment below it,
     * and a pass/fail badge. Good for sharing execution results with stakeholders.
     */
    @Option(names = "--report", description = "Generate an HTML report (command name + comment + pass/fail badge)")
    private boolean report;

    /**
     * Generates a presentation-style HTML file.
     * Each slide shows only the script comment beside the screenshot, vertically
     * centered, with no status indicators. Good for live demos.
     */
    @Option(names = "--present", description = "Generate an HTML presentation (comment only, no status indicators)")
    private boolean present;

    @Option(names = "--pdf", description = "Generate a PDF report")
    private boolean pdf;

    @Option(names = "--video", description = "Generate a video recording")
    private boolean video;

    /**
     * Shorthand that enables report HTML, PDF and video at once.
     * Equivalent to passing --report --pdf --video together.
     */
    @Option(names = "--all", description = "Generate all output formats (report HTML + PDF + video)")
    private boolean all;

    @Option(names = "--theme", description = "HTML theme: PROFESSIONAL, CASUAL, EARTHY. Default: PROFESSIONAL", defaultValue = "PROFESSIONAL")
    private HtmlTheme theme;

    @Option(names = "--format", description = "Video format: MP4, WEBM, GIF. Default: MP4", defaultValue = "MP4")
    private VideoFormat format;

    @Option(names = "--output", description = "Output directory for generated files. Default: ./output", defaultValue = "./output")
    private String outputPath;

    @Override
    public void run() {
        WebDriver driver = null;
        try {
            driver = BrowserFactory.create(this.browser);

            ExecutionContext executionContext = new ExecutionContext();

            VidocScriptParser parser = new VidocScriptParser();
            List<Command> commands = parser.parse(this.scriptPath, executionContext);

            ExecutionEngine engine = new ExecutionEngine();
            engine.run(commands, driver, executionContext);

            if (isReport()) {
                HtmlGenerator gen = new HtmlGenerator(this.theme, HtmlGenerator.Mode.REPORT);
                gen.generate(executionContext, this.outputPath, "report.html");
            }
            if (isPresent()) {
                HtmlGenerator gen = new HtmlGenerator(this.theme, HtmlGenerator.Mode.PRESENT);
                gen.generate(executionContext, this.outputPath, "presentation.html");
            }
            if (isPdf()) {
                new PdfGenerator().generate(executionContext, this.outputPath, "report.pdf");
            }
            if (isVideo()) {
                new VideoGenerator(this.format).generate(executionContext, this.outputPath, "recording.mp4");
            }

        } catch (IOException e) {
            System.err.println("Failed to read script: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public String getScriptPath()  { return this.scriptPath; }
    public BrowserType getBrowser() { return this.browser; }

    /** True when --report or --all is passed. */
    public boolean isReport()   { return this.report || this.all; }

    /** True when --present is passed. */
    public boolean isPresent()  { return this.present; }

    /** True when --pdf or --all is passed. */
    public boolean isPdf()      { return this.pdf || this.all; }

    /** True when --video or --all is passed. */
    public boolean isVideo()    { return this.video || this.all; }
}