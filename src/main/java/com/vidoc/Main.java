package com.vidoc;

import com.vidoc.cli.CliEntry;
import picocli.CommandLine;

/**
 * Entry point for the Vidoc application.
 * <p>
 * Delegates all argument parsing and execution to {@link CliEntry} via picocli.
 * Picocli handles parsing the CLI arguments, validating them, and invoking
 * {@link CliEntry#run()} automatically once parsing succeeds.
 * <p>
 * The exit code returned by picocli is passed to {@link System#exit(int)}
 * so the process exits with the correct code — 0 for success, non-zero for failure.
 * This is important when Vidoc is used in CI pipelines or shell scripts
 * that check exit codes to determine if a step passed or failed.
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args the command line arguments passed by the user
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CliEntry()).execute(args);
        System.exit(exitCode);
    }
}