package com.vidoc.command.interaction;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Selects an option from a dropdown element identified by a CSS selector.
 * Corresponds to: select "#selector" : "option"
 */
public class SelectCommand implements Command {

    private final String selector;
    private final String option;

    public SelectCommand(String selector, String option) {
        this.selector = selector;
        this.option = option;
    }

    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        Select dropdown = new Select(driver.findElement(By.cssSelector(this.selector)));
        dropdown.selectByVisibleText(this.option);
    }
}