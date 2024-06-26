package com.magento.task.objects.ui.controls;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Data;
import lombok.NonNull;
import com.magento.task.framework.utils.FluentWaitUtil;
import com.magento.task.objects.ui.pages.homepage.items.MenuItemEnum;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.LinkedList;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Selenide.$$x;
import static com.magento.task.framework.config.ConfigurationManager.configuration;
import static com.magento.task.framework.utils.WebElementUtils.waitForElementToBeLoaded;

@Data
public class MenuWithElements {
    @NonNull
    private SelenideElement menuButton;
    @NotNull
    private ElementsCollection menuItems;

    public MenuWithElements(SelenideElement menuButton, ElementsCollection menuItems) {
        this.menuButton = menuButton;
        this.menuItems = menuItems;
    }

    public MenuWithElements(SelenideElement menuButton) {
        this.menuButton = menuButton;
        this.menuItems = menuButton.$$x("./*[@role='menu']//*[@role='menuitem']/span");
    }

    public void selectMenuItemByText(MenuItemEnum menuItem) {
        selectMenuItemByText(menuItem.getItem());
    }

    public MenuWithElements scrollTo() {
        this.getMenuButton().scrollTo();
        return this;
    }

    public MenuWithElements waitMenuItemIsLoaded(String menuItemText) {
        if (!configuration().useNativeSeleniumWaits()) {
            Selenide.Wait()
                    .withMessage("Wait menu item to load " + menuItemText + " ...")
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(250))
                    .until(d -> getMenuItems()
                            .asDynamicIterable()
                            .stream()
                            .anyMatch(el -> el
                                    .should(interactable)
                                    .getText().equals(menuItemText)));
        } else {
            FluentWaitUtil
                    .withoutElement(Duration.ofSeconds(10))
                    .withMessage("Wait menu item to load " + menuItemText + " ...")
                    .withPollingInterval(Duration.ofSeconds(1))
                    .untilWithoutElement(d -> getMenuItems()
                            .asDynamicIterable()
                            .stream()
                            .anyMatch(el -> el
                                    .should(interactable)
                                    .getText().equals(menuItemText)));
        }
        return this;
    }

    public SelenideElement getMenuItem(String menuItemText) {
        return getMenuItems()
                .asDynamicIterable()
                .stream()
                .filter(el -> el.getText().equals(menuItemText))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No item available in list: " + menuItemText));
    }

    public void selectMenuItemByText(String menuItemText) {
        waitForElementToBeLoaded(getMenuButton(), Duration.ofSeconds(10));
        getMenuButton().shouldBe(interactable).hover();
        waitMenuItemIsLoaded(menuItemText);
        getMenuItem(menuItemText).click();
    }

    public boolean isDisplayed() {
        return getMenuButton().isDisplayed();
    }

    public void selectNestedMenuItem(String parentMenuItemText, String nestedMenuItemText) {
        waitForElementToBeLoaded(getMenuButton(), Duration.ofSeconds(10));
        getMenuButton().shouldBe(interactable).hover();
        waitMenuItemIsLoaded(parentMenuItemText);
        getMenuItem(parentMenuItemText).shouldBe(interactable).hover();
        waitMenuItemIsLoaded(nestedMenuItemText);
        getMenuItem(nestedMenuItemText).click();
    }

//    public void selectNestedMenuItem(LinkedList<String> locMeNames) {
//        waitForElementToBeLoaded(getMenuButton(), Duration.ofSeconds(10));
//        getMenuButton().shouldBe(interactable).hover();
//
//        for (int i = 0; i < locMeNames.size() - 1; i++) {
//            waitForElementWithTextToBeLoaded(locMeNames.get(i), Duration.ofSeconds(10));
//            getMenuItem(locMeNames.get(i)).shouldBe(interactable).hover();
//        }
//
//        waitForElementWithTextToBeLoaded(locMeNames.getLast(), Duration.ofSeconds(10));
//        getMenuItem(locMeNames.getLast()).click();
//    }

//    private void waitForElementWithTextToBeLoaded(String elementName, Duration timeout) {
//        Selenide.Wait()
//                .withMessage("Wait element with text to be loaded ...")
//                .withTimeout(timeout)
//                .pollingEvery(Duration.ofSeconds(1))
//                .until(d -> d.findElement(By.name(elementName)).isDisplayed());
//    }
}