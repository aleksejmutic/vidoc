package com.vidoc.command;

import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

/**
 * Base class for all Vidoc commands.
 * <p>
 * Holds the comment field so every concrete command gets
 * {@link #setComment} and {@link #getComment} for free without
 * having to implement them individually.
 * Subclasses only need to implement {@link #execute}.
 */
public abstract class BaseCommand implements Command {

    private String comment;

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public abstract void execute(WebDriver driver, ExecutionContext executionContext);
}