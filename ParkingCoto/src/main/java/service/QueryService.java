/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import enums.ParkingSpaceStatus;
import enums.TicketStatus;
import exeption.TicketNotFoundException;
import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;

public class QueryService {

	private final ParkingLot parkingLot;

	public QueryService(ParkingLot parkingLot) {
		this.parkingLot = Objects.requireNonNull(parkingLot, "Parking lot cannot be null");
	}

	public Vehicle findVehicle(String licensePlate) {
		return parkingLot.findVehicle(licensePlate);
	}

	public List<ParkingSpace> getParkingSpaces() {
		return parkingLot.getParkingSpaces();
	}

	public List<ParkingTicket> getActiveTickets() {
		return parkingLot.getActiveTickets();
	}

	public List<ParkingTicket> getTickets() {
		return parkingLot.getTickets();
	}

	public ParkingTicket findActiveTicket(String ticketId) {
		return parkingLot.getActiveTickets().stream()
				.filter(ticket -> ticket.getId().equals(ticketId))
				.findFirst()
				.orElseThrow(() -> new TicketNotFoundException("Active ticket not found: " + ticketId));
	}

	public List<ParkingSpace> getAvailableParkingSpaces() {
		return parkingLot.getParkingSpaces().stream()
				.filter(space -> space.getStatus() == ParkingSpaceStatus.AVAILABLE)
				.collect(Collectors.toList());
	}

	public List<ParkingSpace> getOccupiedParkingSpaces() {
		return parkingLot.getParkingSpaces().stream()
				.filter(space -> space.getStatus() == ParkingSpaceStatus.OCCUPIED)
				.collect(Collectors.toList());
	}

	public BigDecimal getTotalRevenue() {
		return parkingLot.getTickets().stream()
				.filter(ticket -> ticket.getStatus() == TicketStatus.PAID)
				.map(ParkingTicket::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
