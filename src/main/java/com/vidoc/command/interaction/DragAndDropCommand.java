package com.vidoc.command.interaction;

import com.vidoc.command.BaseCommand;
import com.vidoc.command.Command;
import com.vidoc.context.ExecutionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Drags an element and drops it onto another element.
 * Corresponds to: dragAndDrop "#source" : "#target"
 */
public class DragAndDropCommand extends BaseCommand {

    private final String sourceSelector;
    private final String targetSelector;

    public DragAndDropCommand(String sourceSelector, String targetSelector) {
        this.sourceSelector = sourceSelector;
        this.targetSelector = targetSelector;
    }

    /**
     * Locates the source and target elements by their CSS selectors and performs
     * a drag and drop action from source to target.
     * <p>
     * Both the source and target elements are checked for visibility, interactability
     * and size before the action is performed. If either element fails any of these
     * checks, an {@link IllegalArgumentException} is thrown with a descriptive message
     * indicating which element failed and why.
     *
     * @param driver           the Selenium WebDriver instance controlling the browser
     * @param executionContext  the current execution context holding steps and variables
     * @throws IllegalArgumentException if either the source or target element is not
     *                                  visible, not enabled, or has zero dimensions
     */
    @Override
    public void execute(WebDriver driver, ExecutionContext executionContext) {
        WebElement source = driver.findElement(By.cssSelector(this.sourceSelector));
        WebElement target = driver.findElement(By.cssSelector(this.targetSelector));
        validateInteractable(source, this.sourceSelector, "source");
        validateInteractable(target, this.targetSelector, "target");
        new Actions(driver).dragAndDrop(source, target).perform();
    }

    /**
     * Validates that the given element is visible, enabled and has non-zero dimensions.
     *
     * @param element   the element to validate
     * @param selector  the CSS selector used to find the element, included in error messages
     * @param role      describes the role of the element in the command (source or target)
     * @throws IllegalArgumentException if the element fails any interactability check
     */
    private void validateInteractable(WebElement element, String selector, String role) {
        if (!element.isDisplayed()) {
            throw new IllegalArgumentException(
                    "dragAndDrop " + role + " element is not visible: " + selector
            );
        }
        if (!element.isEnabled()) {
            throw new IllegalArgumentException(
                    "dragAndDrop " + role + " element is not enabled: " + selector
            );
        }
        if (element.getSize().getWidth() == 0 || element.getSize().getHeight() == 0) {
            throw new IllegalArgumentException(
                    "dragAndDrop " + role + " element has no size — element may be hidden: " + selector
            );
        }
    }
}