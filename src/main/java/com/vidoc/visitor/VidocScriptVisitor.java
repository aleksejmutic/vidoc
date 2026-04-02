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
 */
public class VidocScriptVisitor extends VidocBaseListener {

    private final List<Command> commands;
    private final ExecutionContext executionContext;

    public VidocScriptVisitor(ExecutionContext executionContext) {
        this.commands = new ArrayList<>();
        this.executionContext = executionContext;
    }

    /**
     * Returns the list of commands built from walking the ASTa.
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
     * Stores the comment text as a pending comment in the context
     * so it can be attached to the next command that executes.
     */
    @Override
    public void enterComment(VidocParser.CommentContext commentContext) {
        String raw = commentContext.COMMENT().getText();
        String trimmedCommentContent = raw.substring(1).trim();
        this.executionContext.setPendingComment(trimmedCommentContent);
    }

    // ─────────────────────────────────────────────
    //  NAVIGATION
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a gotoAction node.
     * Creates a GotoCommand with the URL from the script.
     */
    @Override
    public void enterGotoAction(VidocParser.GotoActionContext ctx) {
        String url = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new GotoCommand(url));
    }

    // ─────────────────────────────────────────────
    //  INTERACTION
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a clickAction node.
     * Creates a ClickCommand with the CSS selector from the script.
     */
    @Override
    public void enterClickAction(VidocParser.ClickActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new ClickCommand(selector));
    }

    /**
     * Called when ANTLR enters a typeAction node.
     * Creates a TypeCommand with the selector and value or variable from the script.
     */
    @Override
    public void enterTypeAction(VidocParser.TypeActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        String value = ctx.stringOrVariable().VAR() != null
                ? ctx.stringOrVariable().VAR().getText()
                : stripQuotes(ctx.stringOrVariable().getText());
        this.commands.add(new TypeCommand(selector, value));
    }

    /**
     * Called when ANTLR enters a clearAction node.
     * Creates a ClearCommand with the CSS selector from the script.
     */
    @Override
    public void enterClearAction(VidocParser.ClearActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new ClearCommand(selector));
    }

    /**
     * Called when ANTLR enters a hoverAction node.
     * Creates a HoverCommand with the CSS selector from the script.
     */
    @Override
    public void enterHoverAction(VidocParser.HoverActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new HoverCommand(selector));
    }

    /**
     * Called when ANTLR enters a scrollToAction node.
     * Creates a ScrollToCommand with the CSS selector from the script.
     */
    @Override
    public void enterScrollToAction(VidocParser.ScrollToActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new ScrollToCommand(selector));
    }

    /**
     * Called when ANTLR enters a scrollUpAction node.
     * Creates a ScrollUpCommand with the pixel count from the script.
     */
    @Override
    public void enterScrollUpAction(VidocParser.ScrollUpActionContext ctx) {
        int pixels = Integer.parseInt(ctx.INT().getText());
        this.commands.add(new ScrollUpCommand(pixels));
    }

    /**
     * Called when ANTLR enters a scrollDownAction node.
     * Creates a ScrollDownCommand with the pixel count from the script.
     */
    @Override
    public void enterScrollDownAction(VidocParser.ScrollDownActionContext ctx) {
        int pixels = Integer.parseInt(ctx.INT().getText());
        this.commands.add(new ScrollDownCommand(pixels));
    }

    /**
     * Called when ANTLR enters a dragAndDropAction node.
     * Creates a DragAndDropCommand with the source and target selectors from the script.
     */
    @Override
    public void enterDragAndDropAction(VidocParser.DragAndDropActionContext ctx) {
        String source = stripQuotes(ctx.selectorValue(0).getText());
        String target = stripQuotes(ctx.selectorValue(1).getText());
        this.commands.add(new DragAndDropCommand(source, target));
    }

    /**
     * Called when ANTLR enters a selectAction node.
     * Creates a SelectCommand with the selector and option text from the script.
     */
    @Override
    public void enterSelectAction(VidocParser.SelectActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        String option = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new SelectCommand(selector, option));
    }

    // ─────────────────────────────────────────────
    //  KEYBOARD
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a pressAction node.
     * Creates a PressCommand with the key name from the script.
     */
    @Override
    public void enterPressAction(VidocParser.PressActionContext ctx) {
        String key = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new PressCommand(key));
    }

    /**
     * Called when ANTLR enters a keyDownAction node.
     * Creates a KeyDownCommand with the key name from the script.
     */
    @Override
    public void enterKeyDownAction(VidocParser.KeyDownActionContext ctx) {
        String key = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new KeyDownCommand(key));
    }

    /**
     * Called when ANTLR enters a keyUpAction node.
     * Creates a KeyUpCommand with the key name from the script.
     */
    @Override
    public void enterKeyUpAction(VidocParser.KeyUpActionContext ctx) {
        String key = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new KeyUpCommand(key));
    }

    // ─────────────────────────────────────────────
    //  WAITING
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a waitAction node.
     * Creates a WaitCommand with the CSS selector from the script.
     */
    @Override
    public void enterWaitAction(VidocParser.WaitActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new WaitCommand(selector));
    }

    /**
     * Called when ANTLR enters a waitForAction node.
     * Creates a WaitForCommand with the number of seconds from the script.
     */
    @Override
    public void enterWaitForAction(VidocParser.WaitForActionContext ctx) {
        int seconds = Integer.parseInt(ctx.INT().getText());
        this.commands.add(new WaitForCommand(seconds));
    }

    // ─────────────────────────────────────────────
    //  ASSERTIONS
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters an assertAction node.
     * Creates an AssertCommand with the selector and expected text from the script.
     */
    @Override
    public void enterAssertAction(VidocParser.AssertActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        String expectedText = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new AssertCommand(selector, expectedText));
    }

    /**
     * Called when ANTLR enters an assertVisibleAction node.
     * Creates an AssertVisibleCommand with the CSS selector from the script.
     */
    @Override
    public void enterAssertVisibleAction(VidocParser.AssertVisibleActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new AssertVisibleCommand(selector));
    }

    /**
     * Called when ANTLR enters an assertHiddenAction node.
     * Creates an AssertHiddenCommand with the CSS selector from the script.
     */
    @Override
    public void enterAssertHiddenAction(VidocParser.AssertHiddenActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new AssertHiddenCommand(selector));
    }

    /**
     * Called when ANTLR enters an assertUrlAction node.
     * Creates an AssertUrlCommand with the expected URL from the script.
     */
    @Override
    public void enterAssertUrlAction(VidocParser.AssertUrlActionContext ctx) {
        String url = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new AssertUrlCommand(url));
    }

    // ─────────────────────────────────────────────
    //  DOCUMENTATION
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a screenshotAction node.
     * Creates a ScreenshotCommand with an optional CSS selector from the script.
     * If no selector is present a full page screenshot will be taken.
     */
    @Override
    public void enterScreenshotAction(VidocParser.ScreenshotActionContext ctx) {
        String selector = ctx.selectorValue() != null
                ? stripQuotes(ctx.selectorValue().getText())
                : null;
        this.commands.add(new ScreenshotCommand(selector));
    }

    /**
     * Called when ANTLR enters a highlightAction node.
     * Creates a HighlightCommand with the CSS selector from the script.
     */
    @Override
    public void enterHighlightAction(VidocParser.HighlightActionContext ctx) {
        String selector = stripQuotes(ctx.selectorValue().getText());
        this.commands.add(new HighlightCommand(selector));
    }

    // ─────────────────────────────────────────────
    //  VARIABLES
    // ─────────────────────────────────────────────

    /**
     * Called when ANTLR enters a setAction node.
     * Creates a SetCommand with the variable name and value from the script.
     * The variable name is stored including the $ prefix.
     */
    @Override
    public void enterSetAction(VidocParser.SetActionContext ctx) {
        String name = ctx.VAR().getText();
        String value = stripQuotes(ctx.stringValue().getText());
        this.commands.add(new SetCommand(name, value));
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