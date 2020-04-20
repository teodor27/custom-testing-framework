package pages;

import model.Item;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainEmagPage extends BasePage {

    private static final String SEARCH_BOX_ID = "searchboxTrigger";
    private static final String POPULAR_SEARCHES_BOLD_CSS = ".searchbox-active strong";
    private static final String SEARCH_BUTTON_CSS = ".searchbox-submit-button";
    private static final String SEARCH_RESULTS_CARDS_CSS = ".card-item.js-product-data";
    private static final String FILTER_BUTTON_CSS = "div:nth-child(2) > div.sort-control-btn-dropdown.hidden-xs > button";
    private static final String FILTER_BUTTON_SPAN_CSS = "div:nth-child(2) > div.sort-control-btn-dropdown.hidden-xs > button > .sort-control-btn-option";
    private static final String NR_REVIEWURI_CSS = "[data-sort-id='reviews']";
    private static final String ITEM_CARDS_REVIEW_CSS = ".star-rating-text  .hidden-xs";
    private static final String ITEM_STAR_RATING_CSS = ".js-product-url .star-rating";
    private static final String ITEM_STAR_NUMBER_CSS = ".js-product-url .star-rating-text .hidden-xs";
    private static final String ITEM_TITLE_CSS = ".product-title-zone a";
    private static final String ITEM_PRICE_CSS = ".product-new-price";
    private static final String NEXT_PAGE_BUTTON_XPATH = "//span[text()='Pagina urmatoare']";
    private static final String COOKIE_PROMPT_CSS = ".js-later";
    private static final String GDPR_COOKIE_BANNER = ".gdpr-cookie-banner";
    private static final String SEO_PAGE_BODY_ID = "#seo-links-body";
    private static final String PRELOADER_CSS = "#card_grid .preloader";
    private static final String RATING_FILTER_CSS = "[data-option-id='1-5']";


    private List<Item> itemlist = new ArrayList<>();

    public void clickSearchBox() {
        this.findElementById(SEARCH_BOX_ID).click();
    }

    public boolean isSearchBoxOpen() {
        this.waitUntilPageIsLoadedByCss(POPULAR_SEARCHES_BOLD_CSS);
        return this.findElementByCssSelector(POPULAR_SEARCHES_BOLD_CSS).isDisplayed();
    }

    public void typeIntoSearchBox(String input) {
        this.findElementById(SEARCH_BOX_ID).sendKeys(input);
    }

    public void pressSearch() {
        this.findElementByCssSelector(SEARCH_BUTTON_CSS).click();
    }

    public boolean areAnyItemsFound() {
        this.waitUntilPageIsLoadedByCss(SEARCH_RESULTS_CARDS_CSS);
        return findElementByCssSelector(SEARCH_RESULTS_CARDS_CSS).isDisplayed();
    }

    public void clickSortButton() {
        this.findElementByCssSelector(FILTER_BUTTON_CSS).click();
    }

    public void sortByReviewNumber() {
        this.findElementByCssSelector(NR_REVIEWURI_CSS).click();
        this.waitUntilPageIsLoadedByCss(PRELOADER_CSS);
        this.waitUntilElementIsInvisible((PRELOADER_CSS), 2);

    }

    public boolean checkPageIsSortedByNumberOfReviews() {
        boolean status = true;
        List<Integer> numberOfRatingsList = new ArrayList<>();
        List<WebElement> elementList = this.findElementsByCssSelector(ITEM_CARDS_REVIEW_CSS);
        for (WebElement element : elementList) {
            String text = element.getText();
            int number = Integer.parseInt(text.split(" ")[0]);
            numberOfRatingsList.add(number);
        }

        for (int i = 0; i < numberOfRatingsList.size() - 1; i++) {
            if (numberOfRatingsList.get(i) < numberOfRatingsList.get(i + 1)) {
                status = false;
                break;
            }
        }
        return status;
    }

    public void closeCookiePrompt() {
        WebElement laterButton = this.shortWaitUntilPageIsLoadedByIdAndClickable(COOKIE_PROMPT_CSS);

        Actions action = new Actions(this.getDriver());
        action.doubleClick(laterButton).build().perform();
    }

    public boolean isCookiePromptClosed() {
        return this.findElementByCssSelector(COOKIE_PROMPT_CSS).isDisplayed();
    }

    public boolean isNextButtonEnabled() {
        boolean status;
        try {
            status = this.findElementByXpath(NEXT_PAGE_BUTTON_XPATH).isEnabled();
        } catch (NoSuchElementException e) {
            status = false;
        }
        return status;
    }

    public void pressNextButton(WebElement element) {
        Actions action = new Actions(this.getDriver());
        action.moveToElement(element).build().perform();
        this.findElementByXpath(NEXT_PAGE_BUTTON_XPATH).click();
        this.waitUntilPageIsLoadedByCss(PRELOADER_CSS);
        this.waitUntilElementIsInvisible((PRELOADER_CSS), 5);
    }

    public void collectInformation() {
        boolean isCollecting = true;
        WebElement seoPageBody = this.findElementByCssSelector(SEO_PAGE_BODY_ID);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        while (isCollecting) {
            int numberOfItemsPerPage = this.findElementsByCssSelector(ITEM_TITLE_CSS).size();
            for (int i = 1; i <= numberOfItemsPerPage; i++) {
                WebElement numberElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_STAR_NUMBER_CSS);
                WebElement ratingElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_STAR_RATING_CSS);
                WebElement titleElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_TITLE_CSS);
                WebElement priceElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_PRICE_CSS);

                Item item = new Item()
                        .setName(titleElement.getAttribute("title"))
                        .setNumberOfReviews(Integer.parseInt(numberElement.getText().split(" ")[0]))
                        .setRating(Double.parseDouble(ratingElement.getAttribute("class").split(" ")[2].split("-")[1]))
                        .setPrice(Double.parseDouble(decimalFormat.format(Double.parseDouble(priceElement.getText().split(" ")[0]) * 1000)));

                if (item.getNumberOfReviews() == 0) {
                    break;
                }

                itemlist.add(item);
            }

            if (isNextButtonEnabled()) {
                this.pressNextButton(seoPageBody);
            } else {
                isCollecting = false;
            }
        }

    }

    public void displayItems() {
        for (Item item : itemlist) {
            System.out.println(item.toString());
        }
    }

    public void collectItemsAboveAverage() {

    }

    public void pressRatingFilter() {
        this.findElementByCssSelector(RATING_FILTER_CSS).click();
        this.waitUntilPageIsLoadedByCss(PRELOADER_CSS);
        this.waitUntilElementIsInvisible((PRELOADER_CSS), 5);
    }
}
