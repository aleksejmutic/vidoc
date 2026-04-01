package com.vidoc.generator;

import com.vidoc.context.ExecutionContext;
import com.vidoc.context.StepResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

/**
 * Generates an interactive HTML presentation from the execution results.
 * <p>
 * Each step in the script is rendered as a slide containing:
 * <ul>
 *   <li>A screenshot of the browser at that step</li>
 *   <li>The comment written above the command in the .visc script</li>
 *   <li>The command name that was executed</li>
 *   <li>A pass or fail status indicator</li>
 * </ul>
 * Navigation between slides is available via Previous and Next buttons
 * and via the left and right keyboard arrow keys. The Previous button
 * is hidden on the first slide and the Next button is hidden on the last.
 * <p>
 * Visual styling is loaded from external CSS files stored in
 * {@code src/main/resources/themes/} — one file per theme. This means
 * new themes can be added by simply dropping a new CSS file into that
 * folder and adding the corresponding entry to {@link HtmlTheme}.
 */
public class HtmlGenerator implements DocumentGenerator {

    private final HtmlTheme theme;

    public HtmlGenerator(HtmlTheme theme) {
        this.theme = theme;
    }

    /**
     * Generates the HTML presentation file and writes it to the output directory.
     * <p>
     * Screenshots are embedded directly into the HTML as base64-encoded data URIs
     * so the presentation is fully self-contained and can be opened without a
     * web server or any external dependencies.
     *
     * @param executionContext the completed context containing all steps and screenshots
     * @param outputPath       the directory where presentation.html will be written
     * @throws RuntimeException if the file cannot be written or the theme CSS cannot be loaded
     */
    @Override
    public void generate(ExecutionContext executionContext, String outputPath) {
        List<StepResult> steps = executionContext.getSteps();
        if (steps.isEmpty()) {
            System.out.println("No steps to generate HTML from.");
            return;
        }
        try {
            new File(outputPath).mkdirs();
            String filePath = outputPath + "/presentation.html";
            FileWriter writer = new FileWriter(filePath);
            writer.write(buildHtml(steps));
            writer.close();
            System.out.println("HTML presentation generated: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate HTML: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the complete HTML document as a string.
     * Iterates over all steps and renders each one as a slide div.
     *
     * @param steps the list of step results to render
     * @return the complete HTML document as a string
     * @throws IOException if a screenshot file cannot be read or the CSS cannot be loaded
     */
    private String buildHtml(List<StepResult> steps) throws IOException {
        StringBuilder slides = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            StepResult step = steps.get(i);
            String imageBase64 = encodeImage(step.getScreenshotPath());
            String comment = step.getComment() != null ? step.getComment() : "";
            boolean success = step.isSuccess();
            String statusClass = success ? "success" : "failure";
            String statusLabel = success ? "✓ Passed" : "✗ Failed";
            String errorHtml = !success && step.getErrorMessage() != null
                    ? "<div class='error-message'>" + step.getErrorMessage() + "</div>"
                    : "";

            slides.append("<div class='slide' id='slide-").append(i).append("'>");
            slides.append("<div class='slide-header'>");
            slides.append("<span class='step-number'>Step ").append(i + 1).append(" of ").append(steps.size()).append("</span>");
            slides.append("<span class='status ").append(statusClass).append("'>").append(statusLabel).append("</span>");
            slides.append("</div>");
            slides.append("<div class='slide-body'>");
            if (imageBase64 != null) {
                slides.append("<div class='screenshot-container'>");
                slides.append("<img src='data:image/png;base64,").append(imageBase64).append("' class='screenshot'/>");
                slides.append("</div>");
            }
            slides.append("<div class='comment-container'>");
            slides.append("<p class='command-name'>").append(step.getCommandName()).append("</p>");
            slides.append("<p class='comment'>").append(comment).append("</p>");
            slides.append(errorHtml);
            slides.append("</div>");
            slides.append("</div>");
            slides.append("<div class='slide-footer'>");
            if (i > 0) {
                slides.append("<button class='btn btn-prev' onclick='goTo(").append(i - 1).append(")'>← Previous</button>");
            } else {
                slides.append("<div></div>");
            }
            if (i < steps.size() - 1) {
                slides.append("<button class='btn btn-next' onclick='goTo(").append(i + 1).append(")'>Next →</button>");
            } else {
                slides.append("<div></div>");
            }
            slides.append("</div>");
            slides.append("</div>");
        }

        return "<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "<meta charset='UTF-8'/>\n" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>\n" +
                "<title>Vidoc Presentation</title>\n" +
                "<style>\n" +
                loadThemeCss() +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class='presentation'>\n" +
                slides +
                "</div>\n" +
                "<script>\n" +
                getJavaScript(steps.size()) +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Loads the CSS file for the selected theme from the classpath resources.
     * <p>
     * Theme files are located at {@code themes/<themename>.css} inside the jar.
     * The theme name is derived from the {@link HtmlTheme} enum value converted
     * to lowercase. Adding a new theme only requires adding a new CSS file and
     * a new enum value — no Java code changes are needed.
     *
     * @return the CSS content as a string
     * @throws IOException if the theme file cannot be found or read
     */
    private String loadThemeCss() throws IOException {
        String resourcePath = "themes/" + this.theme.name().toLowerCase() + ".css";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IOException("Theme CSS not found: " + resourcePath);
        }
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Generates the JavaScript block that handles slide navigation.
     * Sets the first slide as active on load and registers a keydown
     * listener for left and right arrow key navigation.
     *
     * @param totalSlides the total number of slides in the presentation
     * @return the JavaScript as a string
     */
    private String getJavaScript(int totalSlides) {
        return "var current = 0;\n" +
                "function goTo(index) {\n" +
                "  document.getElementById('slide-' + current).classList.remove('active');\n" +
                "  current = index;\n" +
                "  document.getElementById('slide-' + current).classList.add('active');\n" +
                "}\n" +
                "document.getElementById('slide-0').classList.add('active');\n" +
                "document.addEventListener('keydown', function(e) {\n" +
                "  if (e.key === 'ArrowRight' && current < " + (totalSlides - 1) + ") goTo(current + 1);\n" +
                "  if (e.key === 'ArrowLeft' && current > 0) goTo(current - 1);\n" +
                "});\n";
    }

    /**
     * Reads a screenshot file from disk and encodes it as a base64 string
     * so it can be embedded directly into the HTML as a data URI.
     * Returns null if the path is null or the file does not exist.
     *
     * @param screenshotPath absolute path to the screenshot file
     * @return base64 encoded image string, or null if unavailable
     * @throws IOException if the file exists but cannot be read
     */
    private String encodeImage(String screenshotPath) throws IOException {
        if (screenshotPath == null) return null;
        File file = new File(screenshotPath);
        if (!file.exists()) return null;
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }
}