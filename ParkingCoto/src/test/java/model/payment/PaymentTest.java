package model.payment;

import enums.ParkingSpaceType;
import enums.PaymentType;

import exception.InvalidPaymentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;
import model.vehicle.Car;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private static final LocalDateTime ENTRY =
            LocalDateTime.of(
                    2026, 9, 26,
                    10, 0
            );

    @Test
    void shouldCreatePaymentForClosedTicket() {

        ParkingTicket ticket =
                createClosedTicket();

        LocalDateTime paymentTime =
                ENTRY.plusHours(2);

        Payment payment =
                new Payment(
                        ticket,
                        PaymentType.CARD,
                        paymentTime
                );

        assertSame(
                ticket,
                payment.getTicket()
        );

        assertEquals(
                ticket.getId(),
                payment.getTicketId()
        );

        assertEquals(
                new BigDecimal("900"),
                payment.getAmount()
        );

        assertEquals(
                PaymentType.CARD,
                payment.getType()
        );

        assertEquals(
                paymentTime,
                payment.getPaidAt()
        );

        assertNotNull(
                payment.getId()
        );
    }

    @Test
    void shouldRejectPaymentForActiveTicket() {

        ParkingTicket ticket =
                createActiveTicket();

        assertThrows(
                InvalidPaymentException.class,
                () -> new Payment(
                        ticket,
                        PaymentType.CASH,
                        ENTRY
                )
        );
    }

    private ParkingTicket createActiveTicket() {

        Car car =
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        space.park(car);

        return new ParkingTicket(
                car,
                space,
                ENTRY
        );
    }

    private ParkingTicket createClosedTicket() {

        ParkingTicket ticket =
                createActiveTicket();

        ticket.close(
                ENTRY.plusHours(1)
        );

        return ticket;
    }
}