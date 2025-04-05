package com.coveros.training.expenses;

/**
 * Utility class for calculating the distribution of dinner expenses between food and alcohol.
 * <p>
 * This class computes the portion of the total bill allocated to food and alcohol based on the dinner prices.
 * The calculation takes into account the subtotal, tip, and tax provided by the {@link DinnerPrices} object.
 * It also calculates the food ratio, which is the fraction of the subtotal spent on food.
 * </p>
 */
public class AlcoholCalculator {

    /**
     * Calculates the food and alcohol expenses based on the given dinner prices.
     * <p>
     * The process involves the following steps:
     * <ul>
     *     <li>Extract the subtotal, food total, tip, and tax from the provided {@link DinnerPrices}.</li>
     *     <li>Compute the food ratio by dividing the food total by the subtotal and round it to four decimal places.</li>
     *     <li>Calculate the total bill by summing the subtotal, tip, and tax.</li>
     *     <li>Determine the food portion of the bill by multiplying the total bill by the food ratio, rounded to two decimal places.</li>
     *     <li>Determine the alcohol portion as the remainder of the total bill after subtracting the food portion, rounded to two decimal places.</li>
     * </ul>
     * </p>
     *
     * @param dinnerPrices the dinner pricing details including subtotal, food total, tip, and tax
     * @return an {@link AlcoholResult} object containing the calculated food price, alcohol price, and food ratio
     */
    public static AlcoholResult calculate(DinnerPrices dinnerPrices) {
        // Extract dinner price components
        double subTotal = dinnerPrices.getSubtotal();
        double foodTotal = dinnerPrices.getFoodTotal();
        double tip = dinnerPrices.getTip();
        double tax = dinnerPrices.getTax();

        // Calculate food ratio and round to 4 decimals
        double rawFoodRatio = foodTotal / subTotal;
        double foodRatio = Math.round(rawFoodRatio * 10000) / 10000.0;

        // Calculate total bill by summing subtotal, tip, and tax
        double totalBill = subTotal + tip + tax;

        // Calculate the food portion of the bill and round to 2 decimals
        double rawTotalFoodPrice = totalBill * foodRatio;
        double totalFoodPrice = Math.round(rawTotalFoodPrice * 100) / 100.0;

        // Calculate the alcohol portion of the bill and round to 2 decimals
        double rawTotalAlcoholPrice = totalBill * (1 - foodRatio);
        double totalAlcoholPrice = Math.round(rawTotalAlcoholPrice * 100) / 100.0;

        return new AlcoholResult(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }
}
