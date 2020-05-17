package pages;

import model.Item;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.time.Duration.ofMillis;

public class BasePage extends PageObject {

    protected static List<Item> itemList = new ArrayList<>();
    protected String input;

    protected void sleep(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void switchToTab(int tabIndex) {
        ArrayList<String> tabs = new ArrayList<String>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs.get(tabIndex));
    }

    public void closeCurrentTab() {
        getDriver().close();
    }

    protected WebElement findElementById(String id) {
        return getDriver().findElement(By.id(id));
    }

    protected WebElement findElementByCssSelector(String cssSelector) {
        return getDriver().findElement(By.cssSelector(cssSelector));
    }

    protected WebElement findElementByXpath(String xpath) {
        return getDriver().findElement(By.xpath(xpath));
    }

    protected List<WebElement> findElementsByXpath(String xpath) {
        return getDriver().findElements(By.xpath(xpath));
    }

    protected List<WebElement> findElementsByCssSelector(String cssSelector) {
        return getDriver().findElements(By.cssSelector(cssSelector));
    }

    protected WebElement waitUntilPageIsLoadedById(String id) {
        return waitUntilPageIsLoadedByElement(By.id(id), 20, 200);
    }

    protected WebElement waitUntilPageIsLoadedByCss(String css) {
        return waitUntilPageIsLoadedByElement(By.cssSelector(css), 20, 200);
    }

    public void waitUntilPageIsFullyLoaded(int timeOut) {
        FluentWait wait = globalFluentWait(timeOut, 100);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        String status = String.valueOf(js.executeScript("return document.readyState"));
        ExpectedCondition<Boolean> textEqualsString = arg0 -> status.equals("complete");
        wait.until(textEqualsString);
    }

    public void waitUntilElementIsInvisible(String css, int specifiedTimeout) {
        FluentWait wait = globalFluentWait(5, 20);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(css)));
    }

    protected WebElement shortWaitUntilPageIsLoadedByIdAndClickable(String css) {
        return waitUntilPageIsLoadedByElementAndClickable(By.cssSelector(css), 20, 400);

    }

    private WebElement waitUntilPageIsLoadedByElement(By locator, int timeOut, int poolingEvery) {

        FluentWait wait = globalFluentWait(timeOut, poolingEvery);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        return getDriver().findElement(locator);

    }


    private WebElement waitUntilPageIsLoadedByElementAndClickable(By locator, int timeOut, int poolingEvery) {

        FluentWait wait = globalFluentWait(timeOut, poolingEvery);
        wait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator),
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator),
                ExpectedConditions.elementToBeClickable(locator)));

        return getDriver().findElement(locator);
    }


    private FluentWait globalFluentWait(int timeOut, int poolingEvery) {
        FluentWait wait = new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(ofMillis(poolingEvery))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return wait;
    }

}
