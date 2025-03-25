package com.coveros.training.expenses;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.Map;

public class AlcoholStepDefs {

    private AlcoholResult alcoholResult;
    private DinnerPrices dinnerPrices;

    @Given("a dinner with the following prices in dollars:")
    public void a_dinner_with_the_following_prices_in_dollars(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        double subtotal = Double.parseDouble(data.get("subtotal"));
        double foodTotal = Double.parseDouble(data.get("food total"));
        double tip = Double.parseDouble(data.get("tip"));
        double tax = Double.parseDouble(data.get("tax"));
        dinnerPrices = new DinnerPrices(subtotal, foodTotal, tip, tax);
    }

    @When("I calculate the alcohol-related portion")
    public void i_calculate_the_alcohol_related_portion() {
        // Dummy calculation: assume the alcohol-related portion is 10% of the food total,
        // and the food ratio is calculated as foodTotal divided by subtotal.
        double totalFoodPrice = dinnerPrices.getFoodTotal();
        double totalAlcoholPrice = totalFoodPrice * 0.10; // 10% for alcohol
        double foodRatio = dinnerPrices.getSubtotal() != 0 ? totalFoodPrice / dinnerPrices.getSubtotal() : 0.0;
        alcoholResult = new AlcoholResult(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }

    @Then("I get the following results:")
    public void i_get_the_following_results(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        AlcoholResult expectedResult = new AlcoholResult(
                Double.parseDouble(data.get("total food price")),
                Double.parseDouble(data.get("total alcohol price")),
                Double.parseDouble(data.get("food ratio"))
        );
        Assert.assertEquals("The calculated alcohol result does not match the expected result.",
                expectedResult, alcoholResult);
    }
}


