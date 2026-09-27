package model.payment;

import enums.PaymentType;
import exception.InvalidPaymentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import model.ticket.ParkingTicket;
import util.IdGenerator;

public final class Payment {

    private final String id;
    private final ParkingTicket ticket;
    private final BigDecimal amount;
    private final PaymentType type;
    private final LocalDateTime paidAt;

    public Payment(
            ParkingTicket ticket,
            PaymentType type,
            LocalDateTime paidAt) {

        this.ticket = Objects.requireNonNull(
                ticket,
                "Ticket cannot be null"
        );

        if (!ticket.isClosed()) {
            throw new InvalidPaymentException(
                    "Only a closed ticket can generate a payment"
            );
        }

        this.type = Objects.requireNonNull(
                type,
                "Payment type cannot be null"
        );

        this.paidAt = Objects.requireNonNull(
                paidAt,
                "Payment date cannot be null"
        );

        this.amount = validateAmount(
                ticket.getAmount()
        );

        this.id = IdGenerator.nextPaymentId();
    }

    private BigDecimal validateAmount(
            BigDecimal amount) {

        Objects.requireNonNull(
                amount,
                "Payment amount cannot be null"
        );

        if (amount.signum() <= 0) {
            throw new InvalidPaymentException(
                    "Payment amount must be greater than zero"
            );
        }

        return amount;
    }

    public String getId() {
        return id;
    }

    public ParkingTicket getTicket() {
        return ticket;
    }

    public String getTicketId() {
        return ticket.getId();
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