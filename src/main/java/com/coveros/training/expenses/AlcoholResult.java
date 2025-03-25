package com.coveros.training.expenses;

import java.util.Objects;

public class AlcoholResult {

    private double totalFoodPrice;
    private double totalAlcoholPrice;
    private double foodRatio;

    public AlcoholResult(double totalFoodPrice, double totalAlcoholPrice, double foodRatio) {
        this.totalFoodPrice = totalFoodPrice;
        this.totalAlcoholPrice = totalAlcoholPrice;
        this.foodRatio = foodRatio;
    }

    public double getTotalFoodPrice() {
        return totalFoodPrice;
    }

    public double getTotalAlcoholPrice() {
        return totalAlcoholPrice;
    }

    public double getFoodRatio() {
        return foodRatio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlcoholResult that = (AlcoholResult) o;
        return Double.compare(that.totalFoodPrice, totalFoodPrice) == 0 &&
                Double.compare(that.totalAlcoholPrice, totalAlcoholPrice) == 0 &&
                Double.compare(that.foodRatio, foodRatio) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalFoodPrice, totalAlcoholPrice, foodRatio);
    }

    @Override
    public String toString() {
        return "AlcoholResult{" +
                "totalFoodPrice=" + totalFoodPrice +
                ", totalAlcoholPrice=" + totalAlcoholPrice +
                ", foodRatio=" + foodRatio +
                '}';
    }
}
