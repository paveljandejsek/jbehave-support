package org.jbehavesupport.core.internal.web.property;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.web.WebPropertyContext;
import org.jbehavesupport.core.web.WebProperty;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class DefaultWebProperties {

    private final WebDriver driver;

    @Bean
    public WebProperty<Boolean> enabledWebProperty() {
        return new SimpleWebProperty<>("ENABLED", WebElement::isEnabled);
    }

    @Bean
    public WebProperty<Boolean> selectedWebProperty() {
        return new SimpleWebProperty<>("SELECTED", WebElement::isSelected);
    }

    @Bean
    public WebProperty<String> textWebProperty() {
        return new SimpleWebProperty<>("TEXT", WebElement::getText);
    }

    @Bean
    public WebProperty<String> classWebProperty() {
        return new SimpleWebProperty<>("CLASS", e -> e.getAttribute("class"));
    }

    @Bean
    public WebProperty<String> valueWebProperty() {
        return new SimpleWebProperty<>("VALUE", e -> e.getAttribute("value"));
    }

    @Bean
    public WebProperty<Boolean> editableWebProperty() {
        return new SimpleWebProperty<>("EDITABLE", e -> {
            String readonly = e.getAttribute("readonly");
            return e.isEnabled() && (readonly == null || "false".equals(readonly));
        });
    }

    @Bean
    public WebProperty<String> selectedTextWebProperty() {
        return new SimpleWebProperty<>("SELECTED_TEXT",
            e -> e.findElements(By.tagName("option"))
                .stream()
                .filter(o -> o.getAttribute("selected") != null && o.getAttribute("selected").equals("true"))
                .map(WebElement::getText)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new));
    }

    @Bean
    public WebProperty<Integer> rowCountWebProperty() {
        return new SimpleWebProperty<>("ROW_COUNT",
            e -> e.findElement(By.tagName("tbody"))
                .findElements(By.tagName("tr"))
                .size());
    }

    @Bean
    public WebProperty<Boolean> displayedWebProperty() {
        return new AbstractWebProperty<Boolean>() {
            @Override
            public String name() {
                return "DISPLAYED";
            }

            @Override
            public Boolean value(WebPropertyContext ctx) {
                try {
                    return findElement(ctx).isDisplayed();
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        };
    }

    @Bean
    public WebProperty<Boolean> displayedOnScreenWebProperty() {
        return new AbstractWebProperty<Boolean>() {
            @Override
            public String name() {
                return "DISPLAYED_ON_SCREEN";
            }

            @Override
            public Boolean value(WebPropertyContext ctx) {
                try {
                    WebElement element = findElement(ctx);
                    boolean isDisplayed = element.isDisplayed();
                    if (driver instanceof JavascriptExecutor) {
                        boolean displayedOnScreen = (Boolean) ((JavascriptExecutor) driver).executeScript(
                            "var elem = arguments[0],                 " +
                                "  box = elem.getBoundingClientRect(),    " +
                                "  cx = box.left + box.width / 2,         " +
                                "  cy = box.top + box.height / 2,         " +
                                "  e = document.elementFromPoint(cx, cy); " +
                                "for (; e; e = e.parentElement) {         " +
                                "  if (e === elem)                        " +
                                "    return true;                         " +
                                "}                                        " +
                                "return false;                            "
                            , element);
                        return (isDisplayed && displayedOnScreen);
                    } else {
                        throw new AssertionError("web driver is not instance of JavascriptExecutor");
                    }
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        };
    }
}
