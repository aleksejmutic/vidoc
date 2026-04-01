package com.vidoc;

import com.vidoc.cli.CliEntry;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CliEntry()).execute(args);
        System.exit(exitCode);
    }
}