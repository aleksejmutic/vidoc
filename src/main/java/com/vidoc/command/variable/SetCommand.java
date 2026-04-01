package com.vidoc.command.variable;

import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.WebDriver;

/**
 * Declares a variable and stores it in the execution context.
 * Corresponds to: set $variable : "value"
 */
public class SetCommand implements Command {

    private final String name;
    private final String value;

    public SetCommand(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Stores the variable name and value in the execution context so it can
     * be referenced later in the script using the $ prefix. The variable name
     * is stored exactly as written including the $ prefix.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        executionContext.setVariable(this.name, this.value);
    }
}