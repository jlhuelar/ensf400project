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
        alcoholResult = AlcoholCalculator.calculate(dinnerPrices);
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


