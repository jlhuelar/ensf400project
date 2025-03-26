package com.coveros.training.expenses;

public class AlcoholCalculator {
    public static AlcoholResult calculate(DinnerPrices dinnerPrices) {
        // Extract dinner price components
        double subTotal = dinnerPrices.getSubtotal();
        double foodTotal = dinnerPrices.getFoodTotal();
        double tip = dinnerPrices.getTip();
        double tax = dinnerPrices.getTax();

        // Calculate food ratio and round to 4 decimals
        double rawFoodRatio = foodTotal / subTotal;
        double foodRatio = Math.round(rawFoodRatio * 10000) / 10000.0;
        
        // Calculate total bill
        double totalBill = subTotal + tip + tax;
        
        // Calculate the food and alcohol portions and round to 2 decimals
        double rawTotalFoodPrice = totalBill * foodRatio;
        double totalFoodPrice = Math.round(rawTotalFoodPrice * 100) / 100.0;
        
        double rawTotalAlcoholPrice = totalBill * (1 - foodRatio);
        double totalAlcoholPrice = Math.round(rawTotalAlcoholPrice * 100) / 100.0;
        
        return new AlcoholResult(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }
}
