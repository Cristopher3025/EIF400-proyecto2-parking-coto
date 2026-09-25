package model.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import enums.PaymentType;
import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;
import enums.TicketStatus;
import exeption.IncompatibleParkingSpaceException;
import exeption.VehicleAlreadyParkedException;
import model.payment.Payment;
import model.ticket.ParkingTicket;
import model.vehicle.Car;
import service.ExitPaymentService;

class ParkingDomainTest {

    private static final LocalDateTime ENTRY_TIME = LocalDateTime.of(2026, 9, 24, 10, 0);
    private static final LocalDateTime EXIT_TIME = LocalDateTime.of(2026, 9, 24, 12, 30);
    private static final Clock ENTRY_CLOCK = Clock.fixed(
            ENTRY_TIME.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private static final Clock EXIT_CLOCK = Clock.fixed(
            EXIT_TIME.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    @Test
    void shouldRejectVehicleWithIncompatibleParkingSpace() {
        ParkingSpace space = new ParkingSpace("C-01", ParkingSpaceType.CAR);
        Car car = new Car("ABC-123", "Toyota", "Yaris", "Red");

        space.park(car);

        assertEquals(ParkingSpaceStatus.OCCUPIED, space.getStatus());
        assertEquals(car, space.getParkedVehicle());
        assertThrows(IncompatibleParkingSpaceException.class,
                () -> new ParkingSpace("M-01", ParkingSpaceType.MOTORCYCLE).park(car));
    }

    @Test
    void shouldCompleteEntryExitAndPaymentFlow() {
        ParkingLot parkingLot = new ParkingLot(ENTRY_CLOCK);
        parkingLot.registerParkingSpace(new ParkingSpace("C-01", ParkingSpaceType.CAR));
        parkingLot.registerVehicle(new Car("ABC-123", "Toyota", "Yaris", "Red"));

        ParkingTicket ticket = parkingLot.enterVehicle("abc-123");
        assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
        assertThrows(VehicleAlreadyParkedException.class,
                () -> parkingLot.enterVehicle("ABC-123"));

        Payment payment = new ExitPaymentService(parkingLot, EXIT_CLOCK)
                .processExit(ticket.getId(), PaymentType.CARD);

        assertEquals(new BigDecimal("2700"), payment.getAmount());
        assertEquals(TicketStatus.PAID, ticket.getStatus());
        assertEquals(ParkingSpaceStatus.AVAILABLE,
                parkingLot.findParkingSpace("C-01").getStatus());
        assertEquals(0, parkingLot.getActiveTickets().size());
        assertEquals(Instant.from(EXIT_TIME.atOffset(ZoneOffset.UTC)),
                payment.getPaidAt().toInstant(ZoneOffset.UTC));
    }
}