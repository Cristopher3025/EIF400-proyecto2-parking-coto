package service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

import model.parking.ParkingLot;
import model.ticket.ParkingTicket;

public class ExitService {

    private final ParkingLot parkingLot;
    private final Clock clock;

    public ExitService(ParkingLot parkingLot) {
        this(
                parkingLot,
                Clock.systemDefaultZone()
        );
    }

    public ExitService(
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

    public ParkingTicket registerExit(
            String ticketId) {

        LocalDateTime exitTime =
                LocalDateTime.now(clock);

        return parkingLot.closeTicket(
                ticketId,
                exitTime
        );
    }
}