package com.vidoc.generator;

import com.vidoc.context.ExecutionContext;

/**
 * Common interface for all documentation generators.
 * Each generator takes the completed ExecutionContext after
 * a script has finished running and produces an output file
 * in its respective format.
 */
public interface DocumentGenerator {

    /**
     * Generates the output file from the execution results.
     *
     * @param executionContext the completed context containing all steps and screenshots
     * @param outputPath       the directory where the output file should be saved
     */
    void generate(ExecutionContext executionContext, String outputPath);
}