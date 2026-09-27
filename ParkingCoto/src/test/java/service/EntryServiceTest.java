package service;

import enums.ParkingSpaceType;
import enums.TicketStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;
import model.vehicle.Car;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntryServiceTest {

    @Test
    void shouldRegisterEntry() {

        LocalDateTime entry =
                LocalDateTime.of(
                        2026, 9, 26,
                        10, 0
                );

        Clock clock =
                Clock.fixed(
                        entry.toInstant(
                                ZoneOffset.UTC
                        ),
                        ZoneOffset.UTC
                );

        ParkingLot parkingLot =
                new ParkingLot(clock);

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

        EntryService service =
                new EntryService(
                        parkingLot
                );

        ParkingTicket ticket =
                service.registerEntry(
                        "ABC-123"
                );

        assertEquals(
                TicketStatus.ACTIVE,
                ticket.getStatus()
        );

        assertEquals(
                entry,
                ticket.getEntryTime()
        );
    }
}