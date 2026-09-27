package service;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;
import enums.TicketStatus;

import exception.TicketNotFoundException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;
import model.vehicle.Car;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExitServiceTest {

    @Test
    void shouldRegisterExitAndReleaseSpace() {

        LocalDateTime entry =
                LocalDateTime.of(
                        2026, 9, 26,
                        10, 0
                );

        LocalDateTime exit =
                entry.plusMinutes(61);

        ParkingLot parkingLot =
                createParkingLot(
                        entry
                );

        ParkingTicket ticket =
                parkingLot.enterVehicle(
                        "ABC-123"
                );

        ExitService service =
                new ExitService(
                        parkingLot,
                        fixedClock(exit)
                );

        ParkingTicket closedTicket =
                service.registerExit(
                        ticket.getId()
                );

        assertEquals(
                TicketStatus.CLOSED,
                closedTicket.getStatus()
        );

        assertEquals(
                new BigDecimal("1800"),
                closedTicket.getAmount()
        );

        assertEquals(
                ParkingSpaceStatus.AVAILABLE,
                closedTicket
                        .getParkingSpace()
                        .getStatus()
        );

        assertTrue(
                parkingLot
                        .getActiveTickets()
                        .isEmpty()
        );
    }

    @Test
    void shouldRejectExitWithoutActiveTicket() {

        LocalDateTime now =
                LocalDateTime.of(
                        2026, 9, 26,
                        10, 0
                );

        ParkingLot parkingLot =
                new ParkingLot(
                        fixedClock(now)
                );

        ExitService service =
                new ExitService(
                        parkingLot,
                        fixedClock(now)
                );

        assertThrows(
                TicketNotFoundException.class,
                () -> service.registerExit(
                        "T-NOT-FOUND"
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