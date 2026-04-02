package com.vidoc.generator;

import com.vidoc.context.ExecutionContext;
import com.vidoc.context.StepResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

/**
 * Generates a video from the screenshots captured during script execution.
 * <p>
 * Since Selenium captures screenshots per step rather than a continuous
 * screen recording, this generator assembles the screenshots into a
 * slideshow-style video where each step is shown for a fixed duration.
 * <p>
 * Supported output formats are defined in {@link VideoFormat}:
 * MP4 for universal playback, WEBM for web embedding, and GIF for
 * embedding in README files and documentation without a video player.
 * <p>
 * MP4 and WEBM generation delegate to FFmpeg which must be installed
 * on the system. GIF generation is handled entirely in pure Java
 * with no external dependencies.
 */
public class VideoGenerator implements DocumentGenerator {

    private static final int SECONDS_PER_SLIDE = 3;

    private final VideoFormat format;

    public VideoGenerator(VideoFormat format) {
        this.format = format;
    }

    /**
     * Generates the video file from the screenshots in the execution context.
     * <p>
     * The approach differs by format:
     * <ul>
     *   <li>MP4 and WEBM — screenshots are written to a temp folder as numbered
     *       PNG files, then FFmpeg is invoked via a system process to encode them
     *       into a video at the specified frame rate.</li>
     *   <li>GIF — screenshots are assembled into an animated GIF in pure Java
     *       using a simple GIF encoder, with no external tools required.</li>
     * </ul>
     *
     * @param executionContext the completed context containing all steps and screenshots
     * @param outputPath       the directory where the video file will be written
     * @throws RuntimeException if the video cannot be generated
     */
    @Override
    public void generate(ExecutionContext executionContext, String outputPath, String fileName) {
        List<StepResult> steps = executionContext.getSteps();
        if (steps.isEmpty()) {
            System.out.println("No steps to generate video from.");
            return;
        }
        new File(outputPath + fileName).mkdirs();
        try {
            if (this.format == VideoFormat.GIF) {
                generateGif(steps, outputPath);
            } else {
                generateWithFfmpeg(steps, outputPath);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to generate video: " + e.getMessage(), e);
        }
    }

    /**
     * Uses FFmpeg to encode the screenshots into an MP4 or WEBM video.
     * <p>
     * Screenshots are first copied into a numbered sequence in a temp
     * directory so FFmpeg can read them as a frame sequence. FFmpeg must
     * be installed and available on the system PATH.
     *
     * @param steps      the list of step results containing screenshot paths
     * @param outputPath the directory to write the output video into
     * @throws IOException          if screenshots cannot be copied
     * @throws InterruptedException if the FFmpeg process is interrupted
     */
    private void generateWithFfmpeg(List<StepResult> steps, String outputPath)
            throws IOException, InterruptedException {

        File tempDir = new File(outputPath + "/tmp_frames");
        tempDir.mkdirs();

        int frameIndex = 0;
        for (StepResult step : steps) {
            if (step.getScreenshotPath() == null) continue;
            File source = new File(step.getScreenshotPath());
            if (!source.exists()) continue;
            for (int f = 0; f < SECONDS_PER_SLIDE * 1; f++) {
                File dest = new File(tempDir, String.format("frame%04d.png", frameIndex++));
                java.nio.file.Files.copy(source.toPath(), dest.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }

        String extension = this.format == VideoFormat.MP4 ? "mp4" : "webm";
        String outputFile = outputPath + "/recording." + extension;
        String codec = this.format == VideoFormat.MP4 ? "libx264" : "libvpx-vp9";

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-framerate", "1",
                "-i", tempDir.getAbsolutePath() + "/frame%04d.png",
                "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2",
                "-c:v", codec,
                "-pix_fmt", "yuv420p",
                outputFile
        );
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();

        deleteDirectory(tempDir);
        System.out.println("Video generated: " + outputFile);
    }

    /**
     * Generates an animated GIF from the screenshots in pure Java.
     * <p>
     * Each screenshot is shown for {@value SECONDS_PER_SLIDE} seconds.
     * No external tools are required for GIF generation.
     *
     * @param steps      the list of step results containing screenshot paths
     * @param outputPath the directory to write the GIF into
     * @throws IOException if screenshots cannot be read or the GIF cannot be written
     */
    private void generateGif(List<StepResult> steps, String outputPath) throws IOException {
        String outputFile = outputPath + "/recording.gif";
        try (var out = new java.io.FileOutputStream(outputFile)) {
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.start(out);
            encoder.setDelay(SECONDS_PER_SLIDE * 1000);
            encoder.setRepeat(0);
            for (StepResult step : steps) {
                if (step.getScreenshotPath() == null) continue;
                File file = new File(step.getScreenshotPath());
                if (!file.exists()) continue;
                BufferedImage image = ImageIO.read(file);
                if (image != null) encoder.addFrame(image);
            }
            encoder.finish();
        }
        System.out.println("GIF generated: " + outputFile);
    }

    /**
     * Recursively deletes a directory and all its contents.
     * Used to clean up the temporary frame directory after FFmpeg finishes.
     *
     * @param dir the directory to delete
     */
    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) f.delete();
        }
        dir.delete();
    }
}