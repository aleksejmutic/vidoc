package com.vidoc.command.navigation;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

public class GotoCommand implements Command {

    private final String url;

    public GotoCommand(String url) {
        this.url = url;
    }

    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        driver.get(this.url);
    }
}
