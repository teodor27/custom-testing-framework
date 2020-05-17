package pages;

import model.Item;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import util.Input;

public class MainEmagPage extends BasePage {

    private static final String SEARCH_BOX_ID = "searchboxTrigger";
    private static final String POPULAR_SEARCHES_BOLD_CSS = ".searchbox-active strong";
    private static final String SEARCH_BUTTON_CSS = ".searchbox-submit-button";
    private static final String SEARCH_RESULTS_CARDS_CSS = ".card-item.js-product-data";
    private static final String FILTER_BUTTON_CSS = "div:nth-child(2) > div.sort-control-btn-dropdown.hidden-xs > button";
    private static final String NUMBER_OF_REVIEWS_CSS = "[data-sort-id='reviews']";
    private static final String ITEM_STAR_RATING_CSS = ".js-product-url .star-rating";
    private static final String ITEM_STAR_NUMBER_CSS = ".js-product-url .star-rating-text .hidden-xs";
    private static final String ITEM_TITLE_CSS = ".product-title-zone a";
    private static final String ITEM_PRICE_CSS = ".product-new-price";
    private static final String NEXT_PAGE_BUTTON_XPATH = "//span[text()='Pagina urmatoare']";
    private static final String COOKIE_PROMPT_CSS = ".js-later";
    private static final String SEO_PAGE_BODY_ID = "#seo-links-body";
    private static final String PRELOADER_CSS = "#card_grid .preloader";
    private static final String RATING_FILTER_CSS = "[data-option-id='1-5']";

    Input input = new Input();


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

    public void sortByNumberOfReviews() {
        this.findElementByCssSelector(NUMBER_OF_REVIEWS_CSS).click();
        this.waitUntilPageIsLoadedByCss(PRELOADER_CSS);
        this.waitUntilElementIsInvisible((PRELOADER_CSS), 10);

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
        this.waitUntilElementIsInvisible((PRELOADER_CSS), 10);
    }

    public void collectInformation() {
        boolean isCollecting = true;
        WebElement seoPageBody = this.findElementByCssSelector(SEO_PAGE_BODY_ID);
        while (isCollecting) {
            int numberOfItemsPerPage = this.findElementsByCssSelector(ITEM_TITLE_CSS).size();
            for (int i = 1; i <= numberOfItemsPerPage; i++) {
                WebElement numberElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_STAR_NUMBER_CSS);
                WebElement ratingElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_STAR_RATING_CSS);
                WebElement titleElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_TITLE_CSS);
                WebElement priceElement = findElementByCssSelector("[data-position='" + i + "'] " + ITEM_PRICE_CSS);

                String priceText = priceElement.getText().split(" ")[0];
                double correctedPrice;

                if (priceText.contains(".")) {
                    correctedPrice = Double.parseDouble(priceText) * 1000;
                } else {
                    correctedPrice = Double.parseDouble(priceText) / 100;
                }

                Item item = new Item()
                        .setName(titleElement.getAttribute("title"))
                        .setUrl(titleElement.getAttribute("href"))
                        .setNumberOfReviews(Integer.parseInt(numberElement.getText().split(" ")[0]))
                        .setRating(Double.parseDouble(ratingElement.getAttribute("class").split(" ")[2].split("-")[1]))
                        .setPrice((correctedPrice));

                if (item.getNumberOfReviews() == 0) {
                    break;
                }
                itemList.add(item);
            }

            if (isNextButtonEnabled()) {
                this.pressNextButton(seoPageBody);
            } else {
                isCollecting = false;
            }
        }
    }

    public void displayItems() {
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void filterLowNumberItems() {
        double sum = 0;
        for (Item item : itemList) {
            sum += item.getNumberOfReviews();
        }

        double average = sum / itemList.size();
        System.out.println("MEDIA ESTE Number of ratings = " + average);

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getNumberOfReviews() < average) {
                itemList.remove(i);
                i--;
            }
        }

        System.out.println("DIMENSIUNE LISTA dupa nr filter: " + itemList.size());

    }

    public void filterLowRatedItems() {
        double sum = 0;
        for (Item item : itemList) {
            sum += item.getRating();
        }

        double average = sum / itemList.size();
        System.out.println("MEDIA ESTE Ratings = " + average);

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getRating() < average) {
                itemList.remove(i);
                i--;
            }
        }

        System.out.println("DIMENSIUNE LISTA dupa rating filter: " + itemList.size());
    }

    public void filterOverPricedItems(double budget) {
        System.out.println("Buget = " + budget);
        double referencePrice = budget + (10 * budget) / 100;
        System.out.println("PRICE MARGIN = " + referencePrice);

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getPrice() > referencePrice) {
                itemList.remove(i);
                i--;
            }
        }

        System.out.println("DIMENSIUNE LISTA dupa price filter: " + itemList.size());
    }

    public void filterDuplicateItems() {
        System.out.println("LIST BEFORE DUPLICATE FILTER: " + itemList.size());
        for (int i = 0; i < itemList.size() - 1; i++) {
            if (itemList.get(i).getName().split(",")[0].contentEquals(itemList.get(i + 1).getName().split(",")[0]) &&
                    itemList.get(i).getNumberOfReviews() == itemList.get(i + 1).getNumberOfReviews() &&
                    itemList.get(i).getRating() == itemList.get(i + 1).getRating()) {
                itemList.remove(i);
                i--;
            }
        }
        System.out.println("LIST AFTER DUPLICATE FILTER: " + itemList.size());
    }

    public void pressRatingFilter() {
        Actions action = new Actions(getDriver());
        action.moveToElement(findElementByCssSelector(RATING_FILTER_CSS)).build().perform();
        this.findElementByCssSelector(RATING_FILTER_CSS).click();
        this.waitUntilPageIsLoadedByCss(PRELOADER_CSS);
        this.waitUntilElementIsInvisible((PRELOADER_CSS), 10);
    }

    public void reduceBestProductList() {
        int size = itemList.size();
        if (size > 10)
            itemList.subList(10, size).clear();
    }

    public void calculateFinalRatings() {
        for (Item item : itemList) {
            double sum = 0;
            for (int i = 0; i < item.getSelectionPoints().size(); i++) {
                sum += item.getSelectionPoints().get(i);
            }
            double averageRating = (item.getRating() + sum / 1000) / (item.getSelectionPoints().size() + 1);
            item.setRating(averageRating);
            System.out.println("Calculated average rating for " + item.getBrand() + " " + item.getProductCode() + " = " + item.getRating());
        }
    }

    public void reduceToBestProduct() {
        Item max = itemList.get(0);
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getRating() > max.getRating()) {
                max = itemList.get(i);
            } else if (max.getRating() == itemList.get(i).getRating()) {
                if (max.getNumberOfReviews() < itemList.get(i).getNumberOfReviews()) {
                    max = itemList.get(i);
                }
            }
        }
        itemList.clear();
        itemList.add(max);
    }


    public void openProductPage() {
        this.openNewTab(input.getEMagUrl());
        this.switchToTab(1);
        this.sleep(1000);
        System.out.println("Accessing URL: " + itemList.get(0).getUrl());
        getDriver().navigate().to(itemList.get(0).getUrl());
        this.waitUntilPageIsLoadedByCss("body");
    }

    public void navigateToHomePage(String eMagUrl) {
        getDriver().navigate().to(eMagUrl);
    }
}
