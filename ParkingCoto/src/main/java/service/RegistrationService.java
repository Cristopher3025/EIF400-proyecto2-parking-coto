
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
