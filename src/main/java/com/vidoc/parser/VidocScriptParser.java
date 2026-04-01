package com.vidoc.parser;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import com.vidoc.visitor.VidocScriptVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.List;

/**
 * Responsible for reading a .visc script file, running it through
 * the ANTLR lexer and parser, and returning the list of commands
 * extracted by the VidocScriptVisitor.
 * <p>
 * This class is the entry point into the ANTLR pipeline. It wires
 * together the lexer, parser, parse tree walker and visitor so that
 * the rest of the application only needs to call parse() and gets
 * back a clean list of Command objects ready for execution.
 */
public class VidocScriptParser {

    /**
     * Reads the .visc file at the given path, parses it using the
     * ANTLR generated lexer and parser, walks the resulting parse tree
     * with the VidocScriptVisitor, and returns the list of commands.
     *
     * @param scriptPath       path to the .visc script file to parse
     * @param executionContext the execution context that will receive
     *                         pending comments during the tree walk
     * @return the ordered list of Command objects extracted from the script
     * @throws IOException if the script file cannot be read
     */
    public List<Command> parse(String scriptPath, ExecutionContext executionContext) throws IOException {
        VidocLexer lexer = new VidocLexer(CharStreams.fromFileName(scriptPath));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        VidocParser parser = new VidocParser(tokens);
        ParseTree tree = parser.program();
        VidocScriptVisitor visitor = new VidocScriptVisitor(executionContext);
        ParseTreeWalker.DEFAULT.walk(visitor, tree);
        return visitor.getCommands();
    }
}