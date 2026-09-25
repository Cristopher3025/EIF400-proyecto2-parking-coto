/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.Objects;

import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.vehicle.Vehicle;

public class RegistrationService {

	private final ParkingLot parkingLot;

	public RegistrationService(ParkingLot parkingLot) {
		this.parkingLot = Objects.requireNonNull(parkingLot, "Parking lot cannot be null");
	}

	public void registerVehicle(Vehicle vehicle) {
		parkingLot.registerVehicle(vehicle);
	}

	public void registerParkingSpace(ParkingSpace parkingSpace) {
		parkingLot.registerParkingSpace(parkingSpace);
	}
}
