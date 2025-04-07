package com.coveros.training.expenses;

/**
 * Represents the pricing details of a dinner.
 * <p>
 * This class encapsulates the components of a dinner bill including the subtotal, 
 * the amount spent on food, the tip, and the tax.
 * </p>
 */
public class DinnerPrices {
    private final double subTotal;
    private final double foodTotal;
    private final double tip;
    private final double tax;

    /**
     * Constructs a new {@code DinnerPrices} instance with the specified pricing details.
     *
     * @param subTotal  the subtotal of the dinner bill
     * @param foodTotal the amount of the subtotal allocated to food
     * @param tip       the tip amount added to the dinner bill
     * @param tax       the tax amount applied to the dinner bill
     */
    public DinnerPrices(double subTotal, double foodTotal, double tip, double tax) {
        this.subTotal = subTotal;
        this.foodTotal = foodTotal;
        this.tip = tip;
        this.tax = tax;
    }

    /**
     * Returns the subtotal of the dinner bill.
     *
     * @return the subtotal as a double
     */
    public double getSubtotal() {
        return subTotal;
    }

    /**
     * Returns the amount allocated to food in the dinner bill.
     *
     * @return the food total as a double
     */
    public double getFoodTotal() {
        return foodTotal;
    }

    /**
     * Returns the tip amount added to the dinner bill.
     *
     * @return the tip as a double
     */
    public double getTip() {
        return tip;
    }

    /**
     * Returns the tax amount applied to the dinner bill.
     *
     * @return the tax as a double
     */
    public double getTax() {
        return tax;
    }
}
