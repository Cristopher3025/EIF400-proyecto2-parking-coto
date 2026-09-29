
package model.vehicle;

import enums.ParkingSpaceType;
import pricing.ParkingCotoRates;

public class CargoVehicle extends Vehicle {

    public CargoVehicle(
            String licensePlate,
            String brand,
            String model,
            String color) {

        super(
                licensePlate,
                brand,
                model,
                color,
                ParkingCotoRates.cargoVehicle()
        );
    }

    @Override
    public ParkingSpaceType getRequiredSpaceType() {
        return ParkingSpaceType.CARGO;
    }
}
