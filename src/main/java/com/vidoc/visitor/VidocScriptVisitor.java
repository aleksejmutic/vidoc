package com.vidoc.visitor;

import com.vidoc.command.Command;
import com.vidoc.command.navigation.GotoCommand;
import com.vidoc.context.ExecutionContext;
import com.vidoc.parser.VidocBaseListener;
import com.vidoc.parser.VidocParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Walks the ANTLR-generated AST of a parsed .visc script and converts
 * each grammar rule into a corresponding Command object.
 * <p>
 * This is the bridge between the ANTLR world and the command execution world.
 * It is the only class in the project that directly touches ANTLR generated code.
 * Every other class — commands, engine, generators — is pure Java and has no
 * knowledge of ANTLR.
 */
public class VidocScriptVisitor extends VidocBaseListener {

    private final List<Command> commands;
    private final ExecutionContext executionContext;

    public VidocScriptVisitor(ExecutionContext executionContext) {
        this.commands = new ArrayList<>();
        this.executionContext = executionContext;
    }

    /**
     * Returns the list of commands built from walking the AST.
     * Called after the parse tree walk is complete.
     */
    public List<Command> getCommands() {
        return this.commands;
    }

    /**
     * Called when ANTLR enters a comment node in the AST.
     * Stores the comment text as a pending comment in the context
     * so it can be attached to the next command that executes.
     */
    @Override
    public void enterComment(VidocParser.CommentContext commentContext) {
        String raw = commentContext.COMMENT().getText();
        // strip the leading # and trailing newline
        String trimmedCommentContent = raw.substring(1).trim();
        this.executionContext.setPendingComment(trimmedCommentContent);
    }

    /**
     * Called when ANTLR enters a gotoAction node.
     * Creates a GotoCommand with the URL from the script.
     */
    @Override
    public void enterGotoAction(VidocParser.GotoActionContext gotoActionContext) {
        String url = stripQuotes(gotoActionContext.stringValue().getText());
        this.commands.add(new GotoCommand(url));
    }

    // ─────────────────────────────────────────────
    //  UTILITY
    // ─────────────────────────────────────────────

    /**
     * Removes the surrounding quotes from a string token.
     * ANTLR includes the quotes in getText() so "admin" returns "admin"
     * and we need to strip them to get just admin.
     */
    private String stripQuotes(String text) {
        return text.substring(1, text.length() - 1);
    }
}