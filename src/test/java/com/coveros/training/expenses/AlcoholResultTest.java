package com.coveros.training.expenses;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlcoholResultTest {

    @Test
    public void testEqualsAndHashCode() {
        AlcoholResult result1 = new AlcoholResult(10.0, 5.0, 0.6666);
        AlcoholResult result2 = new AlcoholResult(10.0, 5.0, 0.6666);
        assertTrue("Objects should be equal", result1.equals(result2));
        assertEquals("Hash codes should be equal", result1.hashCode(), result2.hashCode());
    }
    
    @Test
    public void testToString() {
        AlcoholResult result = new AlcoholResult(10.0, 5.0, 0.6666);
        String expected = "AlcoholResult{totalFoodPrice=10.0, totalAlcoholPrice=5.0, foodRatio=0.6666}";
        assertEquals("toString output did not match", expected, result.toString());
    }
    
    @Test
    public void testReturnEmpty() {
        AlcoholResult emptyResult = AlcoholResult.returnEmpty();
        AlcoholResult expected = new AlcoholResult(0.0, 0.0, 0.0);
        assertEquals("Empty AlcoholResult did not match", expected, emptyResult);
    }
}
