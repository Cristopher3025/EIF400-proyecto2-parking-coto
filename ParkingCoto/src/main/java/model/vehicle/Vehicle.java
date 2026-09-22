package model.vehicle;

import enums.ParkingSpaceType;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;
import pricing.PricingPolicy;

/**
 *
 * @author Justin PC
 */
public abstract class Vehicle {

    private final String licensePlate;
    private String brand;
    private String model;
    private String color;

    private final PricingPolicy pricingPolicy;

    protected Vehicle(
            String licensePlate,
            String brand,
            String model,
            String color,
            PricingPolicy pricingPolicy) {

        this.licensePlate = validateText(
                licensePlate,
                "License plate"
        ).toUpperCase();

        this.brand = validateText(brand, "Brand");
        this.model = validateText(model, "Model");
        this.color = validateText(color, "Color");

        this.pricingPolicy = Objects.requireNonNull(
                pricingPolicy,
                "Pricing policy cannot be null"
        );
    }

    public abstract ParkingSpaceType getRequiredSpaceType();

    public BigDecimal calculateParkingAmount(Duration duration) {
        return pricingPolicy.calculateAmount(duration);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public void changeBrand(String brand) {
        this.brand = validateText(brand, "Brand");
    }

    public void changeModel(String model) {
        this.model = validateText(model, "Model");
    }

    public void changeColor(String color) {
        this.color = validateText(color, "Color");
    }

    private String validateText(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty"
            );
        }

        return value.trim();
    }

    @Override
    public String toString() {
        return licensePlate
                + " - "
                + brand
                + " "
                + model;
    }
}
