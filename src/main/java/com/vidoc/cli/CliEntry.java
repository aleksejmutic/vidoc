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
 * This class is annotated with picocli's {@code @Command} and implements
 * {@link Runnable} so that picocli can parse the CLI arguments and invoke
 * {@link #run()} automatically. All flags, options and parameters are declared
 * as annotated fields and populated by picocli before {@link #run()} is called.
 * <p>
 * Example usage:
 * <pre>
 *   vidoc login.visc --html
 *   vidoc login.visc --browser BRAVE --all
 *   vidoc login.visc --pdf --video --format WEBM
 *   vidoc login.visc --html --theme EARTHY --output ./docs
 * </pre>
 */
@picocli.CommandLine.Command(
        name = "vidoc",
        mixinStandardHelpOptions = true,
        version = "vidoc 1.0.0",
        description = "Runs a .visc script and generates documentation from it."
)
public class CliEntry implements Runnable {

    /**
     * Path to the .visc script file to execute.
     * This is a required positional argument — it must always be provided.
     */
    @Parameters(index = "0", description = "Path to the .visc script to run")
    private String scriptPath;

    /**
     * The browser to launch for script execution.
     * Supported values: CHROME, FIREFOX, BRAVE, SAFARI.
     * Defaults to CHROME if not specified.
     */
    @Option(names = "--browser", description = "Browser to use: CHROME, FIREFOX, BRAVE, SAFARI. Default: CHROME", defaultValue = "CHROME")
    private BrowserType browser;

    /**
     * If set, an interactive HTML presentation will be generated after execution.
     * Can be combined with --pdf and --video, or replaced by --all.
     */
    @Option(names = "--html", description = "Generate an HTML presentation")
    private boolean html;

    /**
     * If set, a PDF report will be generated after execution.
     * Can be combined with --html and --video, or replaced by --all.
     */
    @Option(names = "--pdf", description = "Generate a PDF report")
    private boolean pdf;

    /**
     * If set, a video recording will be generated after execution.
     * The format is controlled by the --format flag.
     * Can be combined with --html and --pdf, or replaced by --all.
     */
    @Option(names = "--video", description = "Generate a video recording")
    private boolean video;

    /**
     * Shorthand for enabling all output formats at once.
     * Equivalent to passing --html --pdf --video together.
     */
    @Option(names = "--all", description = "Generate all output formats")
    private boolean all;

    /**
     * The visual theme to use for the HTML presentation.
     * Supported values: PROFESSIONAL, CASUAL, EARTHY.
     * Defaults to PROFESSIONAL if not specified.
     * Has no effect if --html or --all is not set.
     */
    @Option(names = "--theme", description = "HTML theme: PROFESSIONAL, CASUAL, EARTHY. Default: PROFESSIONAL", defaultValue = "PROFESSIONAL")
    private HtmlTheme theme;

    /**
     * The video format to use when generating a video recording.
     * Supported values: MP4, WEBM, GIF.
     * Defaults to MP4 if not specified.
     * Has no effect if --video or --all is not set.
     */
    @Option(names = "--format", description = "Video format: MP4, WEBM, GIF. Default: MP4", defaultValue = "MP4")
    private VideoFormat format;

    /**
     * The directory where all generated output files will be saved.
     * The directory will be created if it does not exist.
     * Defaults to ./output in the current working directory.
     */
    @Option(names = "--output", description = "Output directory for generated files. Default: ./output", defaultValue = "./output")
    private String outputPath;

    /**
     * Main execution method invoked by picocli after all arguments are parsed.
     * <p>
     * Orchestrates the full Vidoc pipeline in order:
     * <ol>
     *   <li>Creates a WebDriver instance for the specified browser</li>
     *   <li>Creates a fresh ExecutionContext to accumulate steps and variables</li>
     *   <li>Parses the .visc script into a list of Command objects</li>
     *   <li>Runs the ExecutionEngine which executes each command sequentially</li>
     *   <li>Runs the selected generators to produce the output files</li>
     * </ol>
     * The WebDriver is always closed in the finally block even if an error occurs,
     * so the browser is never left open after execution finishes.
     */
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

            if (isHtml()) {
                new HtmlGenerator(this.theme).generate(executionContext, this.outputPath);
            }
            if (isPdf()) {
                new PdfGenerator().generate(executionContext, this.outputPath);
            }
            if (isVideo()) {
                new VideoGenerator(this.format).generate(executionContext, this.outputPath);
            }

        } catch (IOException e) {
            System.err.println("Failed to read script: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Returns the path to the .visc script provided as a CLI argument.
     *
     * @return the script file path
     */
    public String getScriptPath() {
        return this.scriptPath;
    }

    /**
     * Returns the browser type selected via the --browser flag.
     *
     * @return the selected BrowserType
     */
    public BrowserType getBrowser() {
        return this.browser;
    }

    /**
     * Returns true if an HTML presentation should be generated.
     * This is true when either --html or --all is passed.
     *
     * @return true if HTML output is requested
     */
    public boolean isHtml() {
        return this.html || this.all;
    }

    /**
     * Returns true if a PDF report should be generated.
     * This is true when either --pdf or --all is passed.
     *
     * @return true if PDF output is requested
     */
    public boolean isPdf() {
        return this.pdf || this.all;
    }

    /**
     * Returns true if a video recording should be generated.
     * This is true when either --video or --all is passed.
     *
     * @return true if video output is requested
     */
    public boolean isVideo() {
        return this.video || this.all;
    }
}