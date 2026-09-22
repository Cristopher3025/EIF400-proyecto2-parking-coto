package model.vehicle;

import enums.ParkingSpaceType;
import pricing.ParkingCotoRates;

/**
 *
 * @author Justin PC
 */
public class Motorcycle extends Vehicle {

    public Motorcycle(
            String licensePlate,
            String brand,
            String model,
            String color) {

        super(
                licensePlate,
                brand,
                model,
                color,
                ParkingCotoRates.motorcycle()
        );
    }

    @Override
    public ParkingSpaceType getRequiredSpaceType() {
        return ParkingSpaceType.MOTORCYCLE;
    }
}
