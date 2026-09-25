/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

import enums.PaymentType;
import exeption.InvalidPaymentException;
import model.parking.ParkingLot;
import model.payment.Payment;
import model.ticket.ParkingTicket;

public class ExitPaymentService {

	private final ParkingLot parkingLot;
	private final Clock clock;

	public ExitPaymentService(ParkingLot parkingLot) {
		this(parkingLot, Clock.systemDefaultZone());
	}

	public ExitPaymentService(ParkingLot parkingLot, Clock clock) {
		this.parkingLot = Objects.requireNonNull(parkingLot, "Parking lot cannot be null");
		this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
	}

	public Payment processExit(String ticketId, PaymentType paymentType) {
		if (paymentType == null) {
			throw new InvalidPaymentException("Payment type cannot be null");
		}
		LocalDateTime processedAt = LocalDateTime.now(clock);
		ParkingTicket ticket = parkingLot.closeTicket(ticketId, processedAt);
		Payment payment = new Payment(ticket.getId(), ticket.getAmount(), paymentType, processedAt);
		ticket.markAsPaid();
		return payment;
	}
}
