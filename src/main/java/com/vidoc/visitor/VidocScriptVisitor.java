package com.vidoc.visitor;

import com.vidoc.command.Command;
import com.vidoc.command.assertion.AssertCommand;
import com.vidoc.command.assertion.AssertHiddenCommand;
import com.vidoc.command.assertion.AssertUrlCommand;
import com.vidoc.command.assertion.AssertVisibleCommand;
import com.vidoc.command.documentation.HighlightCommand;
import com.vidoc.command.documentation.ScreenshotCommand;
import com.vidoc.command.interaction.ClickCommand;
import com.vidoc.command.interaction.ClearCommand;
import com.vidoc.command.interaction.DragAndDropCommand;
import com.vidoc.command.interaction.HoverCommand;
import com.vidoc.command.interaction.ScrollDownCommand;
import com.vidoc.command.interaction.ScrollToCommand;
import com.vidoc.command.interaction.ScrollUpCommand;
import com.vidoc.command.interaction.SelectCommand;
import com.vidoc.command.interaction.TypeCommand;
import com.vidoc.command.keyboard.KeyDownCommand;
import com.vidoc.command.keyboard.KeyUpCommand;
import com.vidoc.command.keyboard.PressCommand;
import com.vidoc.command.navigation.GotoCommand;
import com.vidoc.command.variable.SetCommand;
import com.vidoc.command.wait.WaitCommand;
import com.vidoc.command.wait.WaitForCommand;
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
 * <p>
 * Comments are captured into a local {@code pendingComment} field on this visitor.
 * When the next command is entered, the pending comment is baked directly into
 * that command via {@link Command#setComment(String)} before being added to the
 * command list. This ensures each command carries its own comment at parse time,
 * so the engine can later read it from the command rather than from the context.
 */
public class VidocScriptVisitor extends VidocBaseListener {

    private final List<Command> commands;
    private final ExecutionContext executionContext;

    /**
     * Holds the most recently seen # comment.
     * Cleared each time it is consumed by the next command that is entered.
     */
    private String pendingComment;

    public VidocScriptVisitor(ExecutionContext executionContext) {
        this.commands = new ArrayList<>();
        this.executionContext = executionContext;
        this.pendingComment = null;
    }

    /**
     * Returns the list of commands built from walking the AST.
     * Called after the parse tree walk is complete.
     */
    public List<Command> getCommands() {
        return this.commands;
    }

    // ─────────────────────────────────────────────
    //  COMMENT
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a comment node in the AST.
     * Stores the comment text locally so the next command can pick it up.
     * Note: the comment is stored here on the visitor, NOT on the context,
     * because the visitor runs at parse time and must pair each comment
     * with the correct command before execution begins.
     */
    @Override
    public void enterComment(VidocParser.CommentContext commentContext) {
        String raw = commentContext.COMMENT().getText();
        this.pendingComment = raw.substring(1).trim();
    }

    /**
     * Consumes and returns the pending comment, then clears it.
     * Called by every enterXxxAction method so the comment is attached
     * to the command that immediately follows it in the script.
     */
    private String consumePendingComment() {
        String comment = this.pendingComment;
        this.pendingComment = null;
        return comment;
    }

    /**
     * Adds a command to the list, attaching the pending comment to it first.
     * All enterXxxAction methods should use this instead of calling
     * this.commands.add() directly.
     */
    private void addCommand(Command command) {
        command.setComment(consumePendingComment());
        this.commands.add(command);
    }

    // ─────────────────────────────────────────────
    //  NAVIGATION
    // ─────────────────────────────────────────────

    @Override
    public void enterGotoAction(VidocParser.GotoActionContext ctx) {
        addCommand(new GotoCommand(stripQuotes(ctx.stringValue().getText())));
    }

    // ─────────────────────────────────────────────
    //  INTERACTION
    // ─────────────────────────────────────────────

    @Override
    public void enterClickAction(VidocParser.ClickActionContext ctx) {
        addCommand(new ClickCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterTypeAction(VidocParser.TypeActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        String value = ctx.stringOrVariable().VAR() != null
                ? ctx.stringOrVariable().VAR().getText()
                : stripQuotes(ctx.stringOrVariable().getText());
        addCommand(new TypeCommand(selector, value));
    }

    @Override
    public void enterClearAction(VidocParser.ClearActionContext ctx) {
        addCommand(new ClearCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterHoverAction(VidocParser.HoverActionContext ctx) {
        addCommand(new HoverCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterScrollToAction(VidocParser.ScrollToActionContext ctx) {
        addCommand(new ScrollToCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterScrollUpAction(VidocParser.ScrollUpActionContext ctx) {
        addCommand(new ScrollUpCommand(Integer.parseInt(ctx.INT().getText())));
    }

    @Override
    public void enterScrollDownAction(VidocParser.ScrollDownActionContext ctx) {
        addCommand(new ScrollDownCommand(Integer.parseInt(ctx.INT().getText())));
    }

    @Override
    public void enterDragAndDropAction(VidocParser.DragAndDropActionContext ctx) {
        String source = stripQuotes(ctx.selectorValue(0).getText());
        String target = stripQuotes(ctx.selectorValue(1).getText());
        addCommand(new DragAndDropCommand(source, target));
    }

    @Override
    public void enterSelectAction(VidocParser.SelectActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        String option = stripQuotes(ctx.stringValue().getText());
        addCommand(new SelectCommand(selector, option));
    }

    // ─────────────────────────────────────────────
    //  KEYBOARD
    // ─────────────────────────────────────────────

    @Override
    public void enterPressAction(VidocParser.PressActionContext ctx) {
        addCommand(new PressCommand(stripQuotes(ctx.stringValue().getText())));
    }

    @Override
    public void enterKeyDownAction(VidocParser.KeyDownActionContext ctx) {
        addCommand(new KeyDownCommand(stripQuotes(ctx.stringValue().getText())));
    }

    @Override
    public void enterKeyUpAction(VidocParser.KeyUpActionContext ctx) {
        addCommand(new KeyUpCommand(stripQuotes(ctx.stringValue().getText())));
    }

    // ─────────────────────────────────────────────
    //  WAITING
    // ─────────────────────────────────────────────

    @Override
    public void enterWaitAction(VidocParser.WaitActionContext ctx) {
        addCommand(new WaitCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterWaitForAction(VidocParser.WaitForActionContext ctx) {
        addCommand(new WaitForCommand(Integer.parseInt(ctx.INT().getText())));
    }

    // ─────────────────────────────────────────────
    //  ASSERTIONS
    // ─────────────────────────────────────────────

    @Override
    public void enterAssertAction(VidocParser.AssertActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        String expectedText = stripQuotes(ctx.stringValue().getText());
        addCommand(new AssertCommand(selector, expectedText));
    }

    @Override
    public void enterAssertVisibleAction(VidocParser.AssertVisibleActionContext ctx) {
        addCommand(new AssertVisibleCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterAssertHiddenAction(VidocParser.AssertHiddenActionContext ctx) {
        addCommand(new AssertHiddenCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    @Override
    public void enterAssertUrlAction(VidocParser.AssertUrlActionContext ctx) {
        addCommand(new AssertUrlCommand(stripQuotes(ctx.stringValue().getText())));
    }

    // ─────────────────────────────────────────────
    //  DOCUMENTATION
    // ─────────────────────────────────────────────

    @Override
    public void enterScreenshotAction(VidocParser.ScreenshotActionContext ctx) {
        String selector = ctx.selectorValue() != null
                ? stripQuotes(ctx.selectorValue().getText())
                : null;
        addCommand(new ScreenshotCommand(selector));
    }

    @Override
    public void enterHighlightAction(VidocParser.HighlightActionContext ctx) {
        addCommand(new HighlightCommand(stripQuotes(ctx.selectorValue().getText())));
    }

    // ─────────────────────────────────────────────
    //  VARIABLES
    // ─────────────────────────────────────────────

    @Override
    public void enterSetAction(VidocParser.SetActionContext ctx) {
        String name = ctx.VAR().getText();
        String value = stripQuotes(ctx.stringValue().getText());
        addCommand(new SetCommand(name, value));
    }

    // ─────────────────────────────────────────────
    //  UTILITY
    // ─────────────────────────────────────────────

    private String stripQuotes(String text) {
        return text.substring(1, text.length() - 1);
    }
}