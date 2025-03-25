package com.coveros.training.cartesianproduct;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CartesianProductStepDefs {

    // We'll store the list of sets for which we want to compute the cartesian product.
    private List<Set<String>> listOfSets;
    private String result;

    @Given("lists as follows:")
    public void listsAsFollows(DataTable dataTable) {
        List<String> rows = dataTable.asList();
        listOfSets = new ArrayList<>();
        for (String row : rows) {
            // Split the row on commas and trim spaces
            String[] tokens = row.split(",");
            Set<String> set = new LinkedHashSet<>();
            for (String token : tokens) {
                set.add(token.trim());
            }
            listOfSets.add(set);
        }
    }

    @When("we calculate the combinations")
    public void weCalculateTheCombinations() {
        result = CartesianProduct.calculate(listOfSets);
    }

    @Then("the resulting combinations should be as follows:")
    public void theResultingCombinationsShouldBeAsFollows(String expectedResults) {
        // Remove extra whitespace and line breaks for comparison if needed
        String expected = expectedResults.replaceAll("\\s+", " ").trim();
        String actual = result.replaceAll("\\s+", " ").trim();
        Assert.assertEquals("The calculated cartesian product does not match the expected result.",
                expected, actual);
    }
}
