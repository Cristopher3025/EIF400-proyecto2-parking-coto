/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.vehicle;

import enums.ParkingSpaceType;
import pricing.ParkingCotoRates;

/**
 *
 * @author Justin PC
 */
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
