package com.coveros.training.expenses;

import org.junit.Test;
import static org.junit.Assert.*;

public class DinnerPricesTest {

    @Test
    public void testDinnerPricesGetters() {
        DinnerPrices dinnerPrices = new DinnerPrices(33.47, 24.48, 7, 3.99);
        assertEquals(33.47, dinnerPrices.getSubtotal(), 0.001);
        assertEquals(24.48, dinnerPrices.getFoodTotal(), 0.001);
        assertEquals(7, dinnerPrices.getTip(), 0.001);
        assertEquals(3.99, dinnerPrices.getTax(), 0.001);
    }
}
