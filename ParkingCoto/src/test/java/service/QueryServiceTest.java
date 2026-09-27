package service;

import enums.ParkingSpaceType;
import enums.PaymentType;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.ticket.ParkingTicket;
import model.vehicle.Car;
import model.vehicle.Motorcycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryServiceTest {

    private static final LocalDateTime ENTRY =
            LocalDateTime.of(
                    2026, 9, 26,
                    10, 0
            );

    @Test
    void shouldReturnCurrentlyParkedVehicles() {

        ParkingLot parkingLot =
                createParkingLot();

        parkingLot.enterVehicle(
                "CAR-001"
        );

        QueryService service =
                new QueryService(
                        parkingLot
                );

        assertEquals(
                1,
                service.getVehiclesCurrentlyParked()
                        .size()
        );

        assertEquals(
                "CAR-001",
                service.getVehiclesCurrentlyParked()
                        .get(0)
                        .getLicensePlate()
        );
    }

    @Test
    void shouldReturnActiveTickets() {

        ParkingLot parkingLot =
                createParkingLot();

        parkingLot.enterVehicle(
                "CAR-001"
        );

        QueryService service =
                new QueryService(
                        parkingLot
                );

        assertEquals(
                1,
                service.getActiveTicketCount()
        );
    }

    @Test
    void shouldCalculateOccupancyByType() {

        ParkingLot parkingLot =
                createParkingLot();

        parkingLot.enterVehicle(
                "CAR-001"
        );

        parkingLot.enterVehicle(
                "MOTO-001"
        );

        QueryService service =
                new QueryService(
                        parkingLot
                );

        Map<ParkingSpaceType, Long> occupancy =
                service.getOccupancyByType();

        assertEquals(
                1L,
                occupancy.get(
                        ParkingSpaceType.CAR
                )
        );

        assertEquals(
                1L,
                occupancy.get(
                        ParkingSpaceType.MOTORCYCLE
                )
        );

        assertEquals(
                0L,
                occupancy.get(
                        ParkingSpaceType.CARGO
                )
        );
    }

    @Test
    void shouldCalculateTotalRevenueFromPayments() {

        ParkingLot parkingLot =
                createParkingLot();

        ParkingTicket ticket =
                parkingLot.enterVehicle(
                        "CAR-001"
                );

        parkingLot.closeTicket(
                ticket.getId(),
                ENTRY.plusHours(2)
        );

        PaymentService paymentService =
                new PaymentService(
                        parkingLot,
                        fixedClock(
                                ENTRY.plusHours(2)
                        )
                );

        paymentService.registerPayment(
                ticket.getId(),
                PaymentType.CARD
        );

        QueryService queryService =
                new QueryService(
                        parkingLot
                );

        assertEquals(
                new BigDecimal("1800"),
                queryService.getTotalRevenue()
        );
    }

    @Test
    void shouldNotCountClosedUnpaidTicketAsRevenue() {

        ParkingLot parkingLot =
                createParkingLot();

        ParkingTicket ticket =
                parkingLot.enterVehicle(
                        "CAR-001"
                );

        parkingLot.closeTicket(
                ticket.getId(),
                ENTRY.plusHours(2)
        );

        QueryService service =
                new QueryService(
                        parkingLot
                );

        assertEquals(
                BigDecimal.ZERO,
                service.getTotalRevenue()
        );
    }

    private ParkingLot createParkingLot() {

        ParkingLot parkingLot =
                new ParkingLot(
                        fixedClock(ENTRY)
                );

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                )
        );

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "M-01",
                        ParkingSpaceType.MOTORCYCLE
                )
        );

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "G-01",
                        ParkingSpaceType.CARGO
                )
        );

        parkingLot.registerVehicle(
                new Car(
                        "CAR-001",
                        "Toyota",
                        "Yaris",
                        "Red"
                )
        );

        parkingLot.registerVehicle(
                new Motorcycle(
                        "MOTO-001",
                        "Honda",
                        "CBR",
                        "Black"
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