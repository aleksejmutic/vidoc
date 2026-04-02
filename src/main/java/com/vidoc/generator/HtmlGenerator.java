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
 * Two display modes are supported:
 * <ul>
 *   <li><b>Report mode</b>: Shows the command name as a title, the comment below it,
 *       and a pass/fail status badge. Intended for sharing with stakeholders.</li>
 *   <li><b>Present mode</b>: Shows only the comment text, vertically centered beside
 *       the screenshot, with no status indicators. Intended for live demos.</li>
 * </ul>
 * In both modes, screenshots are fixed to 70% of the viewport height and
 * the right-side panel is vertically centered next to them.
 */
public class HtmlGenerator implements DocumentGenerator {

    /** Controls which visual mode is used when rendering slides. */
    public enum Mode {
        /** Full report: command name title + comment + pass/fail badge. */
        REPORT,
        /** Clean presentation: comment only, no status indicators. */
        PRESENT
    }

    private final HtmlTheme theme;
    private final Mode mode;

    public HtmlGenerator(HtmlTheme theme, Mode mode) {
        this.theme = theme;
        this.mode = mode;
    }

    @Override
    public void generate(ExecutionContext executionContext, String outputPath, String fileName) {
        List<StepResult> steps = executionContext.getSteps();
        if (steps.isEmpty()) {
            System.out.println("No steps to generate HTML from.");
            return;
        }
        try {
            new File(outputPath).mkdirs();
            String filePath = outputPath + "/" + fileName;
            FileWriter writer = new FileWriter(filePath);
            writer.write(buildHtml(steps));
            writer.close();
            System.out.println("HTML generated: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate HTML: " + e.getMessage(), e);
        }
    }

    private String buildHtml(List<StepResult> steps) throws IOException {
        StringBuilder slides = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            StepResult step = steps.get(i);
            String imageBase64 = encodeImage(step.getScreenshotPath());
            String comment = step.getComment() != null ? step.getComment() : "";
            boolean success = step.isSuccess();

            slides.append("<div class='slide' id='slide-").append(i).append("'>");

            // ── Header (report mode only) ────────────────────────────────────
            if (mode == Mode.REPORT) {
                String statusClass = success ? "success" : "failure";
                String statusLabel = success ? "✓ Passed" : "✗ Failed";
                slides.append("<div class='slide-header'>");
                slides.append("<span class='step-number'>Step ").append(i + 1).append(" of ").append(steps.size()).append("</span>");
                slides.append("<span class='status ").append(statusClass).append("'>").append(statusLabel).append("</span>");
                slides.append("</div>");
            }

            // ── Body: screenshot + right panel ───────────────────────────────
            slides.append("<div class='slide-body'>");

            // Screenshot
            if (imageBase64 != null) {
                slides.append("<div class='screenshot-container'>");
                slides.append("<img src='data:image/png;base64,").append(imageBase64).append("' class='screenshot'/>");
                slides.append("</div>");
            }

            // Right panel
            slides.append("<div class='info-panel'>");
            if (mode == Mode.REPORT) {
                // Command name as title, comment below
                slides.append("<p class='command-name'>").append(step.getCommandName()).append("</p>");
                if (!comment.isBlank()) {
                    slides.append("<p class='comment'>").append(comment).append("</p>");
                }
                // Error message if failed
                if (!success && step.getErrorMessage() != null) {
                    slides.append("<div class='error-message'>").append(step.getErrorMessage()).append("</div>");
                }
            } else {
                // Present mode: only the comment, vertically centered via CSS
                if (!comment.isBlank()) {
                    slides.append("<p class='comment'>").append(comment).append("</p>");
                }
            }
            slides.append("</div>"); // info-panel

            slides.append("</div>"); // slide-body

            // ── Footer: navigation buttons ───────────────────────────────────
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
            slides.append("</div>"); // slide-footer

            slides.append("</div>"); // slide
        }

        return "<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "<meta charset='UTF-8'/>\n" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>\n" +
                "<title>Vidoc Presentation</title>\n" +
                "<style>\n" +
                loadThemeCss() +
                buildLayoutCss() +
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
     * Layout CSS that is the same regardless of theme.
     * Controls the two-column slide layout, fixed image height,
     * and vertical centering of the info panel.
     */
    private String buildLayoutCss() {
        return "\n" +
                "/* ── Layout overrides ────────────────────────────────────── */\n" +
                ".presentation { max-width: 100%; margin: 0; padding: 0; background: inherit; }\n" +
                ".slide { display: none; flex-direction: column; min-height: 100vh;\n" +
                "         padding: 1.5rem 2rem; box-sizing: border-box; }\n" +
                ".slide.active { display: flex; }\n" +
                ".slide-body {\n" +
                "  display: flex;\n" +
                "  flex-direction: row;\n" +
                "  align-items: center;\n" +
                "  gap: 2rem;\n" +
                "  flex: 1;\n" +
                "}\n" +
                ".screenshot-container {\n" +
                "  flex: 3;\n" +
                "  display: flex;\n" +
                "  justify-content: center;\n" +
                "  align-items: center;\n" +
                "}\n" +
                ".screenshot {\n" +
                "  height: 70vh;\n" +
                "  width: auto;\n" +
                "  max-width: 100%;\n" +
                "  object-fit: contain;\n" +
                "  border-radius: 8px;\n" +
                "  border: 1px solid rgba(0,0,0,0.1);\n" +
                "  box-shadow: 0 4px 24px rgba(0,0,0,0.15);\n" +
                "}\n" +
                ".info-panel {\n" +
                "  flex: 1;\n" +
                "  display: flex;\n" +
                "  flex-direction: column;\n" +
                "  justify-content: center;\n" +   // vertical centering
                "  align-self: stretch;\n" +        // stretch to match screenshot height
                "  padding: 1rem;\n" +
                "  gap: 0.75rem;\n" +
                "}\n" +
                ".command-name {\n" +
                "  font-size: 0.8rem;\n" +
                "  font-weight: 700;\n" +
                "  text-transform: uppercase;\n" +
                "  letter-spacing: 0.08em;\n" +
                "  opacity: 0.55;\n" +
                "  margin: 0;\n" +
                "}\n" +
                ".comment {\n" +
                "  font-size: 1.15rem;\n" +
                "  line-height: 1.65;\n" +
                "  margin: 0;\n" +
                "}\n" +
                ".error-message {\n" +
                "  padding: 0.6rem 0.8rem;\n" +
                "  background: rgba(217,48,37,0.08);\n" +
                "  border-left: 3px solid #d93025;\n" +
                "  border-radius: 4px;\n" +
                "  color: #d93025;\n" +
                "  font-size: 0.85rem;\n" +
                "  font-family: monospace;\n" +
                "  margin: 0;\n" +
                "}\n" +
                ".slide-footer {\n" +
                "  display: flex;\n" +
                "  justify-content: space-between;\n" +
                "  align-items: center;\n" +
                "  padding-top: 1rem;\n" +
                "}\n";
    }

    private String loadThemeCss() throws IOException {
        String resourcePath = "themes/" + this.theme.name().toLowerCase() + ".css";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IOException("Theme CSS not found: " + resourcePath);
        }
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

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

    private String encodeImage(String screenshotPath) throws IOException {
        if (screenshotPath == null) return null;
        File file = new File(screenshotPath);
        if (!file.exists()) return null;
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }
}