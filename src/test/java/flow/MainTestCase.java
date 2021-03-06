package flow;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.junit.Test;
import org.junit.runner.RunWith;
import steps.*;
import util.BaseTestCase;

@RunWith(SerenityRunner.class)
public class MainTestCase extends BaseTestCase {

    @Steps
    MainEmagSteps mainEmagSteps;

    @Steps
    PriceRunnerSteps priceRunnerSteps;

    @Steps
    BaseSteps baseSteps;

    @Steps
    EmagProductSteps emagProductSteps;

    @Steps
    CompariSteps compariSteps;


    @Title("A - Search for item category in Emag Home Page")
    @Test()
    public void test_1() {
        mainEmagSteps.navigateToHomePage();
        mainEmagSteps.selectSearchBox();
        mainEmagSteps.searchForItem();
        mainEmagSteps.checkItemsAreFound();
        mainEmagSteps.closeCookiePrompt();

    }

    @Title("B - Filter by Reviews and collect product data")
    @Test()
    public void test_2() {
        mainEmagSteps.applyLeftSidebarStockFilter();
        mainEmagSteps.applyLeftSidebarRatingFilter();
        mainEmagSteps.displayMostItemsPerPage();
        mainEmagSteps.sortByNewOrMostPopularItems();
        mainEmagSteps.collectInformation();
        mainEmagSteps.filterByBudget();
        mainEmagSteps.filterItemsBelowAverage();
        mainEmagSteps.narrowDownBestProducts();
        emagProductSteps.collectProductInformation();
        baseSteps.closeTab();

    }

    @Title("C - Find reviews in PriceRunner website")
    @Test()
    public void test_3() {
        priceRunnerSteps.openNewPriceRunnerTab();
        priceRunnerSteps.collectRatingInformation();
        baseSteps.closeTab();
    }

    @Title("D - Find reviews and better prices in Compari.ro website")
    @Test()
    public void test_4() {
        compariSteps.openNewCompariTab();
        compariSteps.collectRatingAndOfferInformation();
        baseSteps.closeTab();
    }

    @Title("E - Determine top 3 products")
    @Test()
    public void test_5() {
        mainEmagSteps.determineBestProducts();
        mainEmagSteps.openProductPage(1);
        baseSteps.closeTab();
        mainEmagSteps.openProductPage(2);
        baseSteps.closeTab();
        mainEmagSteps.openProductPage(3);
    }

}
