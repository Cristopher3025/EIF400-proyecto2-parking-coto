package service;

import enums.ParkingSpaceType;
import enums.PaymentType;
import enums.TicketStatus;

import exception.InvalidPaymentException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.payment.Payment;
import model.ticket.ParkingTicket;
import model.vehicle.Car;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Test
    void shouldRejectPaymentForActiveTicket() {

        LocalDateTime entry =
                LocalDateTime.of(
                        2026, 9, 26,
                        10, 0
                );

        ParkingLot parkingLot =
                createParkingLot(entry);

        ParkingTicket ticket =
                parkingLot.enterVehicle(
                        "ABC-123"
                );

        PaymentService service =
                new PaymentService(
                        parkingLot,
                        fixedClock(
                                entry.plusHours(1)
                        )
                );

        assertThrows(
                InvalidPaymentException.class,
                () -> service.registerPayment(
                        ticket.getId(),
                        PaymentType.CASH
                )
        );
    }

    @Test
    void shouldRegisterPaymentForClosedTicket() {

        LocalDateTime entry =
                LocalDateTime.of(
                        2026, 9, 26,
                        10, 0
                );

        LocalDateTime exit =
                entry.plusHours(2);

        ParkingLot parkingLot =
                createParkingLot(entry);

        ParkingTicket ticket =
                parkingLot.enterVehicle(
                        "ABC-123"
                );

        parkingLot.closeTicket(
                ticket.getId(),
                exit
        );

        PaymentService service =
                new PaymentService(
                        parkingLot,
                        fixedClock(exit)
                );

        Payment payment =
                service.registerPayment(
                        ticket.getId(),
                        PaymentType.SINPE_MOVIL
                );

        assertEquals(
                TicketStatus.PAID,
                ticket.getStatus()
        );

        assertEquals(
                new BigDecimal("1800"),
                payment.getAmount()
        );

        assertEquals(
                PaymentType.SINPE_MOVIL,
                payment.getType()
        );

        assertTrue(
                parkingLot.hasPaymentForTicket(
                        ticket.getId()
                )
        );

        assertEquals(
                1,
                parkingLot
                        .getPayments()
                        .size()
        );
    }

    @Test
    void shouldRejectSecondPayment() {

        LocalDateTime entry =
                LocalDateTime.of(
                        2026, 9, 26,
                        10, 0
                );

        LocalDateTime exit =
                entry.plusHours(1);

        ParkingLot parkingLot =
                createParkingLot(entry);

        ParkingTicket ticket =
                parkingLot.enterVehicle(
                        "ABC-123"
                );

        parkingLot.closeTicket(
                ticket.getId(),
                exit
        );

        PaymentService service =
                new PaymentService(
                        parkingLot,
                        fixedClock(exit)
                );

        service.registerPayment(
                ticket.getId(),
                PaymentType.CARD
        );

        assertThrows(
                InvalidPaymentException.class,
                () -> service.registerPayment(
                        ticket.getId(),
                        PaymentType.CARD
                )
        );
    }

    private ParkingLot createParkingLot(
            LocalDateTime entry) {

        ParkingLot parkingLot =
                new ParkingLot(
                        fixedClock(entry)
                );

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                )
        );

        parkingLot.registerVehicle(
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                )
        );

        return parkingLot;
    }

    private Clock fixedClock(
            LocalDateTime dateTime) {

        return Clock.fixed(
                dateTime.toInstant(
                        ZoneOffset.UTC
                ),
                ZoneOffset.UTC
        );
    }
}