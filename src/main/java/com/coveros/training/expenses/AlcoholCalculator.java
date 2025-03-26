package com.coveros.training.expenses;

public class AlcoholCalculator {
    public static AlcoholResult calculate(DinnerPrices dinnerPrices) {
        // Extract dinner price components
        double subTotal = dinnerPrices.getSubtotal();
        double foodTotal = dinnerPrices.getFoodTotal();
        double tip = dinnerPrices.getTip();
        double tax = dinnerPrices.getTax();

        // Calculate food ratio
        double foodRatio = foodTotal / subTotal;
        
        // Calculate total bill
        double totalBill = subTotal + tip + tax;
        
        // Calculate the food and alcohol portions
        double totalFoodPrice = totalBill * foodRatio;
        double totalAlcoholPrice = totalBill * (1 - foodRatio);
        
        return new AlcoholResult(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }
}
