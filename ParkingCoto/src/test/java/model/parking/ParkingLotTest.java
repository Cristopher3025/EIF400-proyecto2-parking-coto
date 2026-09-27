package model.parking;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;
import enums.TicketStatus;

import exception.DuplicateParkingSpaceException;
import exception.DuplicateVehicleException;
import exception.NoCompatibleSpaceException;
import exception.ParkingSpaceNotFoundException;
import exception.VehicleAlreadyParkedException;
import exception.VehicleNotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import model.ticket.ParkingTicket;
import model.vehicle.Car;
import model.vehicle.CargoVehicle;
import model.vehicle.Motorcycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotTest {

    private static final LocalDateTime ENTRY
            = LocalDateTime.of(
                    2026, 9, 26,
                    10, 0
            );

    private static final Clock CLOCK
            = Clock.fixed(
                    ENTRY.toInstant(
                            ZoneOffset.UTC
                    ),
                    ZoneOffset.UTC
            );

    @Test
    void shouldRegisterVehicle() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        Car car
                = new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        parkingLot.registerVehicle(car);

        assertSame(
                car,
                parkingLot.findVehicle("ABC-123")
        );
    }

    @Test
    void shouldRejectDuplicateVehicle() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        parkingLot.registerVehicle(
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                )
        );

        assertThrows(
                DuplicateVehicleException.class,
                () -> parkingLot.registerVehicle(
                        new Car(
                                "ABC-123",
                                "Honda",
                                "Civic",
                                "Blue"
                        )
                )
        );
    }

    @Test
    void shouldRegisterParkingSpace() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        ParkingSpace space
                = new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        parkingLot.registerParkingSpace(space);

        assertSame(
                space,
                parkingLot.findParkingSpace(
                        "C-01"
                )
        );
    }

    @Test
    void shouldFindParkingSpaceIgnoringCaseAndWhitespace() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        ParkingSpace space
                = new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        parkingLot.registerParkingSpace(space);

        assertSame(
                space,
                parkingLot.findParkingSpace(
                        "  c-01  "
                )
        );
    }

    @Test
    void shouldRejectDuplicateParkingSpace() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                )
        );

        assertThrows(
                DuplicateParkingSpaceException.class,
                () -> parkingLot.registerParkingSpace(
                        new ParkingSpace(
                                "c-01",
                                ParkingSpaceType.CAR
                        )
                )
        );
    }

    @Test
    void shouldThrowWhenVehicleDoesNotExist() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        assertThrows(
                VehicleNotFoundException.class,
                () -> parkingLot.findVehicle(
                        "NOT-FOUND"
                )
        );
    }

    @Test
    void shouldThrowWhenParkingSpaceDoesNotExist() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        assertThrows(
                ParkingSpaceNotFoundException.class,
                () -> parkingLot.findParkingSpace(
                        "C-99"
                )
        );
    }

    @Test
    void shouldEnterCarSuccessfully() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
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

        ParkingTicket ticket
                = parkingLot.enterVehicle(
                        "CAR-001"
                );

        assertEquals(
                TicketStatus.ACTIVE,
                ticket.getStatus()
        );

        assertEquals(
                ParkingSpaceStatus.OCCUPIED,
                ticket.getParkingSpace()
                        .getStatus()
        );

        assertEquals(
                ParkingSpaceType.CAR,
                ticket.getParkingSpace()
                        .getType()
        );
    }

    @Test
    void shouldEnterMotorcycleSuccessfully() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "M-01",
                        ParkingSpaceType.MOTORCYCLE
                )
        );

        parkingLot.registerVehicle(
                new Motorcycle(
                        "MOTO-01",
                        "Honda",
                        "CBR",
                        "Black"
                )
        );

        ParkingTicket ticket
                = parkingLot.enterVehicle(
                        "MOTO-01"
                );

        assertEquals(
                ParkingSpaceType.MOTORCYCLE,
                ticket.getParkingSpace()
                        .getType()
        );
    }

    @Test
    void shouldEnterCargoVehicleSuccessfully() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "G-01",
                        ParkingSpaceType.CARGO
                )
        );

        parkingLot.registerVehicle(
                new CargoVehicle(
                        "TRK-001",
                        "Volvo",
                        "FH",
                        "White"
                )
        );

        ParkingTicket ticket
                = parkingLot.enterVehicle(
                        "TRK-001"
                );

        assertEquals(
                ParkingSpaceType.CARGO,
                ticket.getParkingSpace()
                        .getType()
        );
    }

    @Test
    void shouldRejectVehicleWithExistingActiveTicket() {

        ParkingLot parkingLot
                = createParkingLotWithCar();

        parkingLot.enterVehicle(
                "ABC-123"
        );

        assertThrows(
                VehicleAlreadyParkedException.class,
                () -> parkingLot.enterVehicle(
                        "ABC-123"
                )
        );
    }

    @Test
    void shouldRejectEntryWithoutCompatibleAvailableSpace() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

        parkingLot.registerParkingSpace(
                new ParkingSpace(
                        "M-01",
                        ParkingSpaceType.MOTORCYCLE
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

        assertThrows(
                NoCompatibleSpaceException.class,
                () -> parkingLot.enterVehicle(
                        "ABC-123"
                )
        );
    }

    private ParkingLot createParkingLotWithCar() {

        ParkingLot parkingLot
                = new ParkingLot(CLOCK);

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

    @Test
    void shouldIgnoreOutOfServiceSpaceWhenAssigningAutomatically() {

        ParkingLot parkingLot
                = new ParkingLot();

        ParkingSpace outOfServiceSpace
                = new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        ParkingSpace availableSpace
                = new ParkingSpace(
                        "C-02",
                        ParkingSpaceType.CAR
                );

        outOfServiceSpace.markOutOfService();

        parkingLot.registerParkingSpace(
                outOfServiceSpace
        );

        parkingLot.registerParkingSpace(
                availableSpace
        );

        Car car
                = new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        parkingLot.registerVehicle(
                car
        );

        ParkingTicket ticket
                = parkingLot.enterVehicle(
                        "ABC-123"
                );

        assertEquals(
                "C-02",
                ticket.getParkingSpace().getNumber()
        );

        assertEquals(
                ParkingSpaceStatus.OUT_OF_SERVICE,
                outOfServiceSpace.getStatus()
        );

        assertNull(
                outOfServiceSpace.getParkedVehicle()
        );

        assertEquals(
                ParkingSpaceStatus.OCCUPIED,
                availableSpace.getStatus()
        );

        assertEquals(
                car,
                availableSpace.getParkedVehicle()
        );
    }
}
