/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.Objects;

import model.parking.ParkingLot;
import model.ticket.ParkingTicket;

public class EntryService {

	private final ParkingLot parkingLot;

	public EntryService(ParkingLot parkingLot) {
		this.parkingLot = Objects.requireNonNull(parkingLot, "Parking lot cannot be null");
	}

	public ParkingTicket registerEntry(String licensePlate) {
		return parkingLot.enterVehicle(licensePlate);
	}
}
