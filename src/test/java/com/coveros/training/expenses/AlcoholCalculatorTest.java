package com.coveros.training.expenses;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlcoholCalculatorTest {

    @Test
    public void testCalculateOrderingBeerWithDinner() {
        // Given a dinner with the following prices:
        // subtotal: 33.47, food total: 24.48, tip: 7, tax: 3.99
        DinnerPrices dinnerPrices = new DinnerPrices(33.47, 24.48, 7, 3.99);
        
        // When calculating the alcohol-related portion
        AlcoholResult actualResult = AlcoholCalculator.calculate(dinnerPrices);
        
        // Then we expect:
        // totalFoodPrice: 32.52, totalAlcoholPrice: 11.94, foodRatio: 0.7314
        AlcoholResult expectedResult = new AlcoholResult(32.52, 11.94, 0.7314);
        assertEquals("Calculated AlcoholResult did not match the expected result",
                     expectedResult, actualResult);
    }
    
    @Test
    public void testCalculateWithDifferentValues() {
        // Additional test: you can add more scenarios as needed.
        DinnerPrices dinnerPrices = new DinnerPrices(50.0, 35.0, 10.0, 5.0);
        AlcoholResult result = AlcoholCalculator.calculate(dinnerPrices);
        
        // Verify that the calculated food ratio is computed correctly
        double expectedFoodRatio = Math.round((35.0 / 50.0) * 10000) / 10000.0;
        assertEquals(expectedFoodRatio, result.getFoodRatio(), 0.0001);
    }
}
