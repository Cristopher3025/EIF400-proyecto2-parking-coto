/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.parking;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;
import exeption.DuplicateVehicleException;
import exeption.NoCompatibleSpaceException;
import exeption.TicketNotFoundException;
import exeption.VehicleAlreadyParkedException;
import exeption.VehicleNotFoundException;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;

public class ParkingLot {

	private final Clock clock;
	private final Map<String, ParkingSpace> spacesByNumber = new LinkedHashMap<>();
	private final Map<String, Vehicle> vehiclesByPlate = new LinkedHashMap<>();
	private final Map<String, ParkingTicket> activeTicketsById = new LinkedHashMap<>();
	private final Map<String, ParkingTicket> ticketsById = new LinkedHashMap<>();

	public ParkingLot() {
		this(Clock.systemDefaultZone());
	}

	public ParkingLot(Clock clock) {
		this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
	}

	public void registerParkingSpace(ParkingSpace parkingSpace) {
		Objects.requireNonNull(parkingSpace, "Parking space cannot be null");
		if (spacesByNumber.putIfAbsent(parkingSpace.getNumber(), parkingSpace) != null) {
			throw new IllegalArgumentException(
					"Parking space already exists: " + parkingSpace.getNumber());
		}
	}

	public void registerVehicle(Vehicle vehicle) {
		Objects.requireNonNull(vehicle, "Vehicle cannot be null");
		String licensePlate = vehicle.getLicensePlate();
		if (vehiclesByPlate.putIfAbsent(licensePlate, vehicle) != null) {
			throw new DuplicateVehicleException("Vehicle already exists: " + licensePlate);
		}
	}

	public ParkingTicket enterVehicle(String licensePlate) {
		Vehicle vehicle = findVehicle(licensePlate);
		if (findActiveTicketByLicensePlate(licensePlate) != null) {
			throw new VehicleAlreadyParkedException("Vehicle is already parked: " + licensePlate);
		}

		ParkingSpace parkingSpace = findAvailableSpace(vehicle.getRequiredSpaceType());
		parkingSpace.park(vehicle);
		ParkingTicket ticket = new ParkingTicket(vehicle, parkingSpace, LocalDateTime.now(clock));
		activeTicketsById.put(ticket.getId(), ticket);
		ticketsById.put(ticket.getId(), ticket);
		return ticket;
	}

	public ParkingTicket closeTicket(String ticketId, LocalDateTime exitTime) {
		ParkingTicket ticket = activeTicketsById.get(ticketId);
		if (ticket == null) {
			throw new TicketNotFoundException("Active ticket not found: " + ticketId);
		}
		ticket.close(exitTime);
		ticket.getParkingSpace().release();
		activeTicketsById.remove(ticketId);
		return ticket;
	}

	public Vehicle findVehicle(String licensePlate) {
		if (licensePlate == null || licensePlate.isBlank()) {
			throw new VehicleNotFoundException("License plate cannot be empty");
		}
		Vehicle vehicle = vehiclesByPlate.get(licensePlate.trim().toUpperCase());
		if (vehicle == null) {
			throw new VehicleNotFoundException("Vehicle not found: " + licensePlate);
		}
		return vehicle;
	}

	public ParkingSpace findParkingSpace(String number) {
		ParkingSpace parkingSpace = spacesByNumber.get(number);
		if (parkingSpace == null) {
			throw new IllegalArgumentException("Parking space not found: " + number);
		}
		return parkingSpace;
	}

	public List<ParkingSpace> getParkingSpaces() {
		return Collections.unmodifiableList(new ArrayList<>(spacesByNumber.values()));
	}

	public List<ParkingTicket> getActiveTickets() {
		return Collections.unmodifiableList(new ArrayList<>(activeTicketsById.values()));
	}

	public List<ParkingTicket> getTickets() {
		return Collections.unmodifiableList(new ArrayList<>(ticketsById.values()));
	}

	private ParkingSpace findAvailableSpace(ParkingSpaceType type) {
		return spacesByNumber.values().stream()
				.filter(space -> space.getType() == type)
				.filter(space -> space.getStatus() == ParkingSpaceStatus.AVAILABLE)
				.findFirst()
				.orElseThrow(() -> new NoCompatibleSpaceException(
						"No available parking space for type: " + type));
	}

	private ParkingTicket findActiveTicketByLicensePlate(String licensePlate) {
		return activeTicketsById.values().stream()
				.filter(ticket -> ticket.getVehicle().getLicensePlate().equals(licensePlate.trim().toUpperCase()))
				.findFirst()
				.orElse(null);
	}
}
