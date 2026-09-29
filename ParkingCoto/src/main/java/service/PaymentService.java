package service;

import enums.PaymentType;
import exception.InvalidPaymentException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

import model.parking.ParkingLot;
import model.payment.Payment;
import model.ticket.ParkingTicket;

public class PaymentService {

    private final ParkingLot parkingLot;
    private final Clock clock;

    public PaymentService(
            ParkingLot parkingLot) {

        this(
                parkingLot,
                Clock.systemDefaultZone()
        );
    }

    public PaymentService(
            ParkingLot parkingLot,
            Clock clock) {

        this.parkingLot =
                Objects.requireNonNull(
                        parkingLot,
                        "Parking lot cannot be null"
                );

        this.clock =
                Objects.requireNonNull(
                        clock,
                        "Clock cannot be null"
                );
    }

    public Payment registerPayment(
            String ticketId,
            PaymentType paymentType) {

        if (paymentType == null) {
            throw new InvalidPaymentException(
                    "Payment type cannot be null"
            );
        }

        ParkingTicket ticket =
                parkingLot.findTicket(ticketId);

        if (ticket.isActive()) {
            throw new InvalidPaymentException(
                    "An active ticket cannot be paid"
            );
        }

        if (ticket.isPaid()) {
            throw new InvalidPaymentException(
                    "Ticket has already been paid"
            );
        }

        if (parkingLot.hasPaymentForTicket(
                ticketId)) {

            throw new InvalidPaymentException(
                    "A payment already exists for ticket: "
                    + ticketId
            );
        }

        Payment payment =
                new Payment(
                        ticket,
                        paymentType,
                        LocalDateTime.now(clock)
                );

        parkingLot.registerPayment(payment);

        ticket.markAsPaid();

        return payment;
    }
}
