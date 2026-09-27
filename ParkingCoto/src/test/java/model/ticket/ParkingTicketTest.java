package model.ticket;

import enums.ParkingSpaceType;
import enums.TicketStatus;

import exception.InvalidTicketStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import model.parking.ParkingSpace;
import model.vehicle.Car;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingTicketTest {

    private static final LocalDateTime ENTRY =
            LocalDateTime.of(
                    2026, 9, 26,
                    10, 0
            );

    @Test
    void shouldStartActive() {

        ParkingTicket ticket =
                createTicket();

        assertEquals(
                TicketStatus.ACTIVE,
                ticket.getStatus()
        );

        assertTrue(ticket.isActive());
        assertFalse(ticket.isClosed());
        assertFalse(ticket.isPaid());

        assertNull(ticket.getExitTime());
        assertNull(ticket.getAmount());
    }

    @Test
    void shouldCloseTicketAndCalculateAmount() {

        ParkingTicket ticket =
                createTicket();

        BigDecimal amount =
                ticket.close(
                        ENTRY.plusMinutes(61)
                );

        assertEquals(
                new BigDecimal("1800"),
                amount
        );

        assertEquals(
                TicketStatus.CLOSED,
                ticket.getStatus()
        );

        assertEquals(
                ENTRY.plusMinutes(61),
                ticket.getExitTime()
        );
    }

    @Test
    void shouldRejectSecondClose() {

        ParkingTicket ticket =
                createTicket();

        ticket.close(
                ENTRY.plusHours(1)
        );

        assertThrows(
                InvalidTicketStatusException.class,
                () -> ticket.close(
                        ENTRY.plusHours(2)
                )
        );
    }

    @Test
    void shouldRejectExitBeforeEntry() {

        ParkingTicket ticket =
                createTicket();

        assertThrows(
                IllegalArgumentException.class,
                () -> ticket.close(
                        ENTRY.minusMinutes(1)
                )
        );
    }

    @Test
    void shouldMarkClosedTicketAsPaid() {

        ParkingTicket ticket =
                createTicket();

        ticket.close(
                ENTRY.plusHours(1)
        );

        ticket.markAsPaid();

        assertEquals(
                TicketStatus.PAID,
                ticket.getStatus()
        );

        assertTrue(ticket.isPaid());
    }

    @Test
    void shouldRejectPaymentStateOnActiveTicket() {

        ParkingTicket ticket =
                createTicket();

        assertThrows(
                InvalidTicketStatusException.class,
                ticket::markAsPaid
        );
    }

    private ParkingTicket createTicket() {

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
}