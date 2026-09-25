/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ticket;

import enums.TicketStatus;
import exeption.InvalidTicketStatusException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import model.parking.ParkingSpace;
import model.vehicle.Vehicle;
import util.IdGenerator;

public class ParkingTicket {

	private final String id;
	private final Vehicle vehicle;
	private final ParkingSpace parkingSpace;
	private final LocalDateTime entryTime;
	private TicketStatus status;
	private LocalDateTime exitTime;
	private BigDecimal amount;

	public ParkingTicket(Vehicle vehicle, ParkingSpace parkingSpace, LocalDateTime entryTime) {
		this.id = IdGenerator.nextTicketId();
		this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null");
		this.parkingSpace = Objects.requireNonNull(parkingSpace, "Parking space cannot be null");
		this.entryTime = Objects.requireNonNull(entryTime, "Entry time cannot be null");
		this.status = TicketStatus.ACTIVE;
	}

	public BigDecimal close(LocalDateTime exitTime) {
		if (status != TicketStatus.ACTIVE) {
			throw new InvalidTicketStatusException("Only active tickets can be closed");
		}
		Objects.requireNonNull(exitTime, "Exit time cannot be null");
		if (exitTime.isBefore(entryTime)) {
			throw new IllegalArgumentException("Exit time cannot be before entry time");
		}
		this.exitTime = exitTime;
		this.amount = vehicle.calculateParkingAmount(Duration.between(entryTime, exitTime));
		this.status = TicketStatus.CLOSED;
		return amount;
	}

	public void markAsPaid() {
		if (status != TicketStatus.CLOSED) {
			throw new InvalidTicketStatusException("Only closed tickets can be marked as paid");
		}
		status = TicketStatus.PAID;
	}

	public String getId() {
		return id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public ParkingSpace getParkingSpace() {
		return parkingSpace;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
