package model.vehicle;

import enums.ParkingSpaceType;
import pricing.ParkingCotoRates;

/**
 *
 * @author Justin PC
 */
public class Car extends Vehicle {

    public Car(
            String licensePlate,
            String brand,
            String model,
            String color) {

        super(
                licensePlate,
                brand,
                model,
                color,
                ParkingCotoRates.car()
        );
    }

    @Override
    public ParkingSpaceType getRequiredSpaceType() {
        return ParkingSpaceType.CAR;
    }
}
