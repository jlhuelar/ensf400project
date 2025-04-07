package com.coveros.training.expenses;

import java.util.Objects;

/**
 * Represents the result of the alcohol calculation by holding the computed food and alcohol prices
 * along with the food ratio used in the calculation.
 */
public class AlcoholResult {

    private double totalFoodPrice;
    private double totalAlcoholPrice;
    private double foodRatio;

    /**
     * Constructs an {@code AlcoholResult} with the specified values.
     *
     * @param totalFoodPrice   the calculated total price for food
     * @param totalAlcoholPrice the calculated total price for alcohol
     * @param foodRatio        the ratio of the subtotal allocated to food
     */
    public AlcoholResult(double totalFoodPrice, double totalAlcoholPrice, double foodRatio) {
        this.totalFoodPrice = totalFoodPrice;
        this.totalAlcoholPrice = totalAlcoholPrice;
        this.foodRatio = foodRatio;
    }

    /**
     * Returns the total food price.
     *
     * @return the total food price as a double
     */
    public double getTotalFoodPrice() {
        return totalFoodPrice;
    }

    /**
     * Returns the total alcohol price.
     *
     * @return the total alcohol price as a double
     */
    public double getTotalAlcoholPrice() {
        return totalAlcoholPrice;
    }

    /**
     * Returns the food ratio used in the calculation.
     *
     * @return the food ratio as a double
     */
    public double getFoodRatio() {
        return foodRatio;
    }

    /**
     * Creates and returns an empty {@code AlcoholResult} with all values set to zero.
     *
     * @return an {@code AlcoholResult} instance with zero values for food price, alcohol price, and food ratio
     */
    public static AlcoholResult returnEmpty() {
        return new AlcoholResult(0.0, 0.0, 0.0);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this object is the same as the {@code o} argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlcoholResult that = (AlcoholResult) o;
        return Double.compare(that.totalFoodPrice, totalFoodPrice) == 0 &&
               Double.compare(that.totalAlcoholPrice, totalAlcoholPrice) == 0 &&
               Double.compare(that.foodRatio, foodRatio) == 0;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string containing the values of the fields in the {@code AlcoholResult}
     */
    @Override
    public String toString() {
        return "AlcoholResult{" +
                "totalFoodPrice=" + totalFoodPrice +
                ", totalAlcoholPrice=" + totalAlcoholPrice +
                ", foodRatio=" + foodRatio +
                '}';
    }
}
