package com.vidoc.command;

import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

public interface Command {
    void execute(WebDriver driver, ExecutionContext executionContext);
}
