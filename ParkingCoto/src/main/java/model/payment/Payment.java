/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import enums.PaymentType;

public final class Payment {

	private final String ticketId;
	private final BigDecimal amount;
	private final PaymentType type;
	private final LocalDateTime paidAt;

	public Payment(String ticketId, BigDecimal amount, PaymentType type, LocalDateTime paidAt) {
		if (ticketId == null || ticketId.isBlank()) {
			throw new IllegalArgumentException("Ticket ID cannot be empty");
		}
		this.ticketId = ticketId;
		this.amount = validateAmount(amount);
		this.type = Objects.requireNonNull(type, "Payment type cannot be null");
		this.paidAt = Objects.requireNonNull(paidAt, "Payment date cannot be null");
	}

	private BigDecimal validateAmount(BigDecimal value) {
		Objects.requireNonNull(value, "Payment amount cannot be null");
		if (value.signum() <= 0) {
			throw new IllegalArgumentException("Payment amount must be greater than zero");
		}
		return value;
	}

	public String getTicketId() {
		return ticketId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public PaymentType getType() {
		return type;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}
}
