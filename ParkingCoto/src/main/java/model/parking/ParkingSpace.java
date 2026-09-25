/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.parking;

import java.util.Objects;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;
import exeption.IncompatibleParkingSpaceException;
import exeption.ParkingException;
import exeption.ParkingSpaceUnavailableException;
import model.vehicle.Vehicle;

public class ParkingSpace {

	private final String number;
	private final ParkingSpaceType type;
	private ParkingSpaceStatus status;
	private Vehicle parkedVehicle;

	public ParkingSpace(String number, ParkingSpaceType type) {
		if (number == null || number.isBlank()) {
			throw new IllegalArgumentException("Parking space number cannot be empty");
		}
		this.number = number.trim();
		this.type = Objects.requireNonNull(type, "Parking space type cannot be null");
		this.status = ParkingSpaceStatus.AVAILABLE;
	}

	public void park(Vehicle vehicle) {
		Objects.requireNonNull(vehicle, "Vehicle cannot be null");
		if (status != ParkingSpaceStatus.AVAILABLE) {
			throw new ParkingSpaceUnavailableException("Parking space is not available: " + number);
		}
		if (vehicle.getRequiredSpaceType() != type) {
			throw new IncompatibleParkingSpaceException(
					"Vehicle is incompatible with parking space " + number);
		}
		parkedVehicle = vehicle;
		status = ParkingSpaceStatus.OCCUPIED;
	}

	public Vehicle release() {
		if (status != ParkingSpaceStatus.OCCUPIED) {
			throw new ParkingException("Parking space is not occupied: " + number);
		}
		Vehicle vehicle = parkedVehicle;
		parkedVehicle = null;
		status = ParkingSpaceStatus.AVAILABLE;
		return vehicle;
	}

	public void markOutOfService() {
		if (status == ParkingSpaceStatus.OCCUPIED) {
			throw new ParkingException("Occupied parking space cannot be taken out of service");
		}
		status = ParkingSpaceStatus.OUT_OF_SERVICE;
	}

	public String getNumber() {
		return number;
	}

	public ParkingSpaceType getType() {
		return type;
	}

	public ParkingSpaceStatus getStatus() {
		return status;
	}

	public Vehicle getParkedVehicle() {
		return parkedVehicle;
	}
}
