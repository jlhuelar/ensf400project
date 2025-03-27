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

    private List<Set<String>> listOfSets;
    private String result;

    @Given("lists as follows:")
    public void listsAsFollows(DataTable dataTable) {
        // Get the table as a list of lists of strings
        List<List<String>> rows = dataTable.asLists(String.class);
        listOfSets = new ArrayList<>();

        // Skip the header row (index 0) and process the rest
        for (int i = 1; i < rows.size(); i++) {
            String row = rows.get(i).get(0);
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
        // Normalize whitespace if needed
        String expected = expectedResults.replaceAll("\\s+", " ").trim();
        String actual = result.replaceAll("\\s+", " ").trim();
        Assert.assertEquals("The calculated cartesian product does not match the expected result.",
                expected, actual);
    }
}
