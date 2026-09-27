package model.parking;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;
import exception.IncompatibleParkingSpaceException;
import exception.ParkingSpaceUnavailableException;

import java.util.Objects;

import model.vehicle.Vehicle;

public class ParkingSpace {

    private final String number;
    private final ParkingSpaceType type;

    private ParkingSpaceStatus status;
    private Vehicle parkedVehicle;

    public ParkingSpace(
            String number,
            ParkingSpaceType type) {

        this.number = validateNumber(number);

        this.type = Objects.requireNonNull(
                type,
                "Parking space type cannot be null"
        );

        this.status = ParkingSpaceStatus.AVAILABLE;
    }

    public void park(Vehicle vehicle) {

        Objects.requireNonNull(
                vehicle,
                "Vehicle cannot be null"
        );

        if (status != ParkingSpaceStatus.AVAILABLE) {
            throw new ParkingSpaceUnavailableException(
                    "Parking space "
                    + number
                    + " is not available"
            );
        }

        if (vehicle.getRequiredSpaceType() != type) {
            throw new IncompatibleParkingSpaceException(
                    "Vehicle "
                    + vehicle.getLicensePlate()
                    + " is not compatible with parking space "
                    + number
            );
        }

        parkedVehicle = vehicle;
        status = ParkingSpaceStatus.OCCUPIED;
    }

    public void release() {

        if (status != ParkingSpaceStatus.OCCUPIED) {
            throw new ParkingSpaceUnavailableException(
                    "Only an occupied parking space can be released"
            );
        }

        parkedVehicle = null;
        status = ParkingSpaceStatus.AVAILABLE;
    }

    public void markOutOfService() {

        if (status == ParkingSpaceStatus.OCCUPIED) {
            throw new ParkingSpaceUnavailableException(
                    "An occupied parking space cannot be marked out of service"
            );
        }

        if (status == ParkingSpaceStatus.OUT_OF_SERVICE) {
            throw new ParkingSpaceUnavailableException(
                    "Parking space is already out of service"
            );
        }

        status = ParkingSpaceStatus.OUT_OF_SERVICE;
    }

    public void restoreService() {

        if (status != ParkingSpaceStatus.OUT_OF_SERVICE) {
            throw new ParkingSpaceUnavailableException(
                    "Only an out-of-service parking space can be restored"
            );
        }

        status = ParkingSpaceStatus.AVAILABLE;
    }

    private String validateNumber(String number) {

        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException(
                    "Parking space number cannot be empty"
            );
        }

        return number.trim().toUpperCase();
    }

    public boolean isAvailable() {
        return status == ParkingSpaceStatus.AVAILABLE;
    }

    public boolean isOccupied() {
        return status == ParkingSpaceStatus.OCCUPIED;
    }

    public boolean isOutOfService() {
        return status == ParkingSpaceStatus.OUT_OF_SERVICE;
    }

    public String getNumber() {
        return number;
    }

    public ParkingSpaceType getType() {
        return type;
    }

    public ParkingSpaceStatus getStatus() {
        return status;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }
}