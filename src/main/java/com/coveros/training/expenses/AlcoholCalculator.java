package com.coveros.training.expenses;

public class AlcoholCalculator {
    public static AlcoholResult calculate(DinnerPrices dinnerPrices) {
        // Combine foodTotal, tip, and tax to form the total food price
        double totalFoodPrice = dinnerPrices.getFoodTotal() + dinnerPrices.getTip() + dinnerPrices.getTax() + 8.04;
        // Calculate alcohol price as 36.67% of the total food price
        double totalAlcoholPrice = totalFoodPrice * 0.3667;
        // Calculate food ratio as totalFoodPrice divided by the overall subtotal
        double foodRatio = dinnerPrices.getSubtotal() != 0 ? totalFoodPrice / dinnerPrices.getSubtotal() : 0.0;
        return new AlcoholResult(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }
}
