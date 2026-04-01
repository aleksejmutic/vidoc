package com.vidoc.generator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vidoc.context.ExecutionContext;
import com.vidoc.context.StepResult;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Generates a PDF report from the execution results using OpenPDF.
 * <p>
 * Each step in the script is rendered as a separate page in the PDF.
 * Every page contains the step number, the comment that described the
 * step, the command that was executed, a pass or fail status indicator,
 * the screenshot taken at that step, and the error message if the step
 * failed.
 * <p>
 * The PDF is fully self-contained — screenshots are embedded directly
 * into the document so no external files are needed to view it.
 */
public class PdfGenerator implements DocumentGenerator {

    private static final Color COLOR_PRIMARY = new Color(26, 115, 232);
    private static final Color COLOR_SUCCESS = new Color(30, 142, 62);
    private static final Color COLOR_FAILURE = new Color(217, 48, 37);
    private static final Color COLOR_BACKGROUND = new Color(248, 249, 250);
    private static final Color COLOR_BORDER = new Color(218, 220, 224);
    private static final Color COLOR_TEXT = new Color(32, 33, 36);

    /**
     * Generates the PDF report file and writes it to the output directory.
     * <p>
     * Each step is written as a separate page. If a step has no screenshot
     * the image section is skipped and only the text content is rendered.
     *
     * @param executionContext the completed context containing all steps and screenshots
     * @param outputPath       the directory where report.pdf will be written
     * @throws RuntimeException if the PDF cannot be written
     */
    @Override
    public void generate(ExecutionContext executionContext, String outputPath) {
        List<StepResult> steps = executionContext.getSteps();
        if (steps.isEmpty()) {
            System.out.println("No steps to generate PDF from.");
            return;
        }
        try {
            new File(outputPath).mkdirs();
            String filePath = outputPath + "/report.pdf";
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            for (int i = 0; i < steps.size(); i++) {
                if (i > 0) document.newPage();
                renderStep(document, steps.get(i), i + 1, steps.size());
            }
            document.close();
            System.out.println("PDF report generated: " + filePath);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Renders a single step as a page in the PDF document.
     * <p>
     * The page is laid out with a header section showing the step number
     * and status, a body section with the screenshot and comment side by
     * side, and an error section shown only when the step failed.
     *
     * @param document    the OpenPDF document to write into
     * @param step        the step result to render
     * @param stepNumber  the 1-based index of this step
     * @param totalSteps  the total number of steps in the script
     * @throws DocumentException if OpenPDF fails to add content
     * @throws IOException       if the screenshot file cannot be read
     */
    private void renderStep(Document document, StepResult step, int stepNumber, int totalSteps)
            throws DocumentException, IOException {

        // header
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3, 1});
        header.setSpacingAfter(16);

        Font stepFont = new Font(Font.HELVETICA, 11, Font.NORMAL, COLOR_TEXT);
        PdfPCell stepCell = new PdfPCell(new Phrase("Step " + stepNumber + " of " + totalSteps, stepFont));
        stepCell.setBorder(Rectangle.NO_BORDER);
        stepCell.setBackgroundColor(COLOR_BACKGROUND);
        stepCell.setPadding(8);
        header.addCell(stepCell);

        boolean success = step.isSuccess();
        Color statusColor = success ? COLOR_SUCCESS : COLOR_FAILURE;
        String statusText = success ? "✓ Passed" : "✗ Failed";
        Font statusFont = new Font(Font.HELVETICA, 10, Font.BOLD, statusColor);
        PdfPCell statusCell = new PdfPCell(new Phrase(statusText, statusFont));
        statusCell.setBorder(Rectangle.NO_BORDER);
        statusCell.setBackgroundColor(COLOR_BACKGROUND);
        statusCell.setPadding(8);
        statusCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        header.addCell(statusCell);

        document.add(header);

        // command name
        Font commandFont = new Font(Font.HELVETICA, 9, Font.BOLD, COLOR_PRIMARY);
        Paragraph commandParagraph = new Paragraph(step.getCommandName(), commandFont);
        commandParagraph.setSpacingAfter(4);
        document.add(commandParagraph);

        // comment
        String comment = step.getComment() != null ? step.getComment() : "";
        Font commentFont = new Font(Font.HELVETICA, 12, Font.NORMAL, COLOR_TEXT);
        Paragraph commentParagraph = new Paragraph(comment, commentFont);
        commentParagraph.setSpacingAfter(16);
        document.add(commentParagraph);

        // screenshot
        if (step.getScreenshotPath() != null) {
            File screenshotFile = new File(step.getScreenshotPath());
            if (screenshotFile.exists()) {
                Image image = Image.getInstance(step.getScreenshotPath());
                image.scaleToFit(document.getPageSize().getWidth() - 72,
                        document.getPageSize().getHeight() - 200);
                image.setBorderColor(COLOR_BORDER);
                image.setBorder(Image.BOX);
                image.setBorderWidth(1);
                document.add(image);
            }
        }

        // error message
        if (!success && step.getErrorMessage() != null) {
            Font errorFont = new Font(Font.COURIER, 9, Font.NORMAL, COLOR_FAILURE);
            Paragraph errorParagraph = new Paragraph(step.getErrorMessage(), errorFont);
            errorParagraph.setSpacingBefore(12);
            document.add(errorParagraph);
        }
    }
}