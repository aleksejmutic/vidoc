package com.vidoc.generator;

import com.vidoc.context.ExecutionContext;

public interface DocumentGenerator {
    void generate(ExecutionContext executionContext, String outputPath);
}
