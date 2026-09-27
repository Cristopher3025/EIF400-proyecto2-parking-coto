package model.parking;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;

import exception.IncompatibleParkingSpaceException;
import exception.ParkingSpaceUnavailableException;

import model.vehicle.Car;
import model.vehicle.Motorcycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpaceTest {

    @Test
    void shouldStartAvailable() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        assertEquals(
                ParkingSpaceStatus.AVAILABLE,
                space.getStatus()
        );

        assertNull(
                space.getParkedVehicle()
        );
    }

    @Test
    void shouldParkCompatibleVehicle() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        Car car =
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        space.park(car);

        assertEquals(
                ParkingSpaceStatus.OCCUPIED,
                space.getStatus()
        );

        assertSame(
                car,
                space.getParkedVehicle()
        );
    }

    @Test
    void shouldRejectIncompatibleVehicle() {

        ParkingSpace space =
                new ParkingSpace(
                        "M-01",
                        ParkingSpaceType.MOTORCYCLE
                );

        Car car =
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        assertThrows(
                IncompatibleParkingSpaceException.class,
                () -> space.park(car)
        );
    }

    @Test
    void shouldRejectSecondVehicleWhenOccupied() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        Car firstCar =
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        Car secondCar =
                new Car(
                        "DEF-456",
                        "Honda",
                        "Civic",
                        "Blue"
                );

        space.park(firstCar);

        assertThrows(
                ParkingSpaceUnavailableException.class,
                () -> space.park(secondCar)
        );
    }

    @Test
    void shouldRejectVehicleWhenOutOfService() {

        ParkingSpace space =
                new ParkingSpace(
                        "M-01",
                        ParkingSpaceType.MOTORCYCLE
                );

        Motorcycle motorcycle =
                new Motorcycle(
                        "M-123",
                        "Honda",
                        "CBR",
                        "Black"
                );

        space.markOutOfService();

        assertThrows(
                ParkingSpaceUnavailableException.class,
                () -> space.park(motorcycle)
        );
    }

    @Test
    void shouldReleaseOccupiedSpace() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        Car car =
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        space.park(car);
        space.release();

        assertEquals(
                ParkingSpaceStatus.AVAILABLE,
                space.getStatus()
        );

        assertNull(
                space.getParkedVehicle()
        );
    }

    @Test
    void shouldMarkAvailableSpaceOutOfService() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        space.markOutOfService();

        assertEquals(
                ParkingSpaceStatus.OUT_OF_SERVICE,
                space.getStatus()
        );
    }

    @Test
    void shouldNotMarkOccupiedSpaceOutOfService() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        space.park(
                new Car(
                        "ABC-123",
                        "Toyota",
                        "Yaris",
                        "Red"
                )
        );

        assertThrows(
                ParkingSpaceUnavailableException.class,
                space::markOutOfService
        );
    }

    @Test
    void shouldRestoreOutOfServiceSpace() {

        ParkingSpace space =
                new ParkingSpace(
                        "C-01",
                        ParkingSpaceType.CAR
                );

        space.markOutOfService();
        space.restoreService();

        assertEquals(
                ParkingSpaceStatus.AVAILABLE,
                space.getStatus()
        );
    }

    @Test
    void shouldNormalizeParkingSpaceNumber() {

        ParkingSpace space =
                new ParkingSpace(
                        "  c-01  ",
                        ParkingSpaceType.CAR
                );

        assertEquals(
                "C-01",
                space.getNumber()
        );
    }
}