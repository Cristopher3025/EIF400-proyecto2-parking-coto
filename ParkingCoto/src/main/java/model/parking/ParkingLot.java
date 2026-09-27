package model.parking;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;

import exception.DuplicateParkingSpaceException;
import exception.DuplicateVehicleException;
import exception.NoCompatibleSpaceException;
import exception.ParkingSpaceNotFoundException;
import exception.TicketNotFoundException;
import exception.VehicleAlreadyParkedException;
import exception.VehicleNotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.payment.Payment;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;

public class ParkingLot {

    private final Clock clock;

    private final Map<String, ParkingSpace> spacesByNumber;
    private final Map<String, Vehicle> vehiclesByPlate;
    private final Map<String, ParkingTicket> activeTicketsById;
    private final Map<String, ParkingTicket> ticketsById;
    private final Map<String, Payment> paymentsById;

    public ParkingLot() {
        this(Clock.systemDefaultZone());
    }

    public ParkingLot(Clock clock) {

        this.clock = Objects.requireNonNull(
                clock,
                "Clock cannot be null"
        );

        this.spacesByNumber = new LinkedHashMap<>();
        this.vehiclesByPlate = new LinkedHashMap<>();
        this.activeTicketsById = new LinkedHashMap<>();
        this.ticketsById = new LinkedHashMap<>();
        this.paymentsById = new LinkedHashMap<>();
    }

    // =================================================
    // REGISTRATION
    // =================================================
    public void registerParkingSpace(
            ParkingSpace parkingSpace) {

        Objects.requireNonNull(
                parkingSpace,
                "Parking space cannot be null"
        );

        String number
                = parkingSpace.getNumber();

        if (spacesByNumber.putIfAbsent(
                number,
                parkingSpace) != null) {

            throw new DuplicateParkingSpaceException(
                    "Parking space already exists: "
                    + number
            );
        }
    }

    public void registerVehicle(
            Vehicle vehicle) {

        Objects.requireNonNull(
                vehicle,
                "Vehicle cannot be null"
        );

        String licensePlate
                = vehicle.getLicensePlate();

        if (vehiclesByPlate.putIfAbsent(
                licensePlate,
                vehicle) != null) {

            throw new DuplicateVehicleException(
                    "Vehicle already exists: "
                    + licensePlate
            );
        }
    }

    // =================================================
    // ENTRY
    // =================================================
    public ParkingTicket enterVehicle(
            String licensePlate) {

        Vehicle vehicle
                = findVehicle(licensePlate);

        ParkingTicket activeTicket
                = findActiveTicketByLicensePlate(
                        vehicle.getLicensePlate()
                );

        if (activeTicket != null) {

            throw new VehicleAlreadyParkedException(
                    "Vehicle is already parked: "
                    + vehicle.getLicensePlate()
            );
        }

        ParkingSpace parkingSpace
                = findAvailableSpace(
                        vehicle.getRequiredSpaceType()
                );

        parkingSpace.park(vehicle);

        ParkingTicket ticket
                = new ParkingTicket(
                        vehicle,
                        parkingSpace,
                        LocalDateTime.now(clock)
                );

        activeTicketsById.put(
                ticket.getId(),
                ticket
        );

        ticketsById.put(
                ticket.getId(),
                ticket
        );

        return ticket;
    }

    // =================================================
    // EXIT
    // =================================================
    public ParkingTicket closeTicket(
            String ticketId,
            LocalDateTime exitTime) {

        Objects.requireNonNull(
                exitTime,
                "Exit time cannot be null"
        );

        ParkingTicket ticket
                = findActiveTicket(ticketId);

        /*
         * The ticket must be successfully closed before
         * changing the state of the parking space.
         *
         * If close() fails, the space remains occupied
         * and the ticket remains active.
         */
        ticket.close(exitTime);

        /*
         * At this point the vehicle has successfully
         * registered its exit from the parking lot.
         */
        ticket.getParkingSpace().release();

        activeTicketsById.remove(
                ticket.getId()
        );

        return ticket;
    }

    // =================================================
    // PAYMENTS
    // =================================================
    public void registerPayment(
            Payment payment) {

        Objects.requireNonNull(
                payment,
                "Payment cannot be null"
        );

        if (paymentsById.putIfAbsent(
                payment.getId(),
                payment) != null) {

            throw new IllegalArgumentException(
                    "Payment already exists: "
                    + payment.getId()
            );
        }
    }

    public boolean hasPaymentForTicket(
            String ticketId) {

        if (ticketId == null
                || ticketId.isBlank()) {

            return false;
        }

        return paymentsById.values()
                .stream()
                .anyMatch(payment
                        -> payment.getTicketId()
                        .equals(ticketId)
                );
    }

    // =================================================
    // SEARCH
    // =================================================
    public Vehicle findVehicle(
            String licensePlate) {

        if (licensePlate == null
                || licensePlate.isBlank()) {

            throw new VehicleNotFoundException(
                    "License plate cannot be empty"
            );
        }

        String normalizedPlate
                = licensePlate
                        .trim()
                        .toUpperCase();

        Vehicle vehicle
                = vehiclesByPlate.get(
                        normalizedPlate
                );

        if (vehicle == null) {

            throw new VehicleNotFoundException(
                    "Vehicle not found: "
                    + licensePlate
            );
        }

        return vehicle;
    }

    public ParkingSpace findParkingSpace(
            String number) {

        if (number == null
                || number.isBlank()) {

            throw new ParkingSpaceNotFoundException(
                    "Parking space number cannot be empty"
            );
        }

        String normalizedNumber
                = number
                        .trim()
                        .toUpperCase();

        ParkingSpace parkingSpace
                = spacesByNumber.get(
                        normalizedNumber
                );

        if (parkingSpace == null) {

            throw new ParkingSpaceNotFoundException(
                    "Parking space not found: "
                    + number
            );
        }

        return parkingSpace;
    }

    public ParkingTicket findTicket(
            String ticketId) {

        if (ticketId == null
                || ticketId.isBlank()) {

            throw new TicketNotFoundException(
                    "Ticket id cannot be empty"
            );
        }

        ParkingTicket ticket
                = ticketsById.get(ticketId);

        if (ticket == null) {

            throw new TicketNotFoundException(
                    "Ticket not found: "
                    + ticketId
            );
        }

        return ticket;
    }

    public ParkingTicket findActiveTicket(
            String ticketId) {

        if (ticketId == null
                || ticketId.isBlank()) {

            throw new TicketNotFoundException(
                    "Ticket id cannot be empty"
            );
        }

        ParkingTicket ticket
                = activeTicketsById.get(
                        ticketId
                );

        if (ticket == null) {

            throw new TicketNotFoundException(
                    "Active ticket not found: "
                    + ticketId
            );
        }

        return ticket;
    }

    // =================================================
    // READ-ONLY COLLECTIONS
    // =================================================
    public List<Vehicle> getVehicles() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        vehiclesByPlate.values()
                )
        );
    }

    public List<ParkingSpace>
            getParkingSpaces() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        spacesByNumber.values()
                )
        );
    }

    public List<ParkingTicket>
            getActiveTickets() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        activeTicketsById.values()
                )
        );
    }

    public List<ParkingTicket>
            getTickets() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        ticketsById.values()
                )
        );
    }

    public List<Payment>
            getPayments() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        paymentsById.values()
                )
        );
    }

    // =================================================
    // INTERNAL DOMAIN OPERATIONS
    // =================================================
    private ParkingSpace findAvailableSpace(
            ParkingSpaceType type) {

        Objects.requireNonNull(
                type,
                "Parking space type cannot be null"
        );

        return spacesByNumber.values()
                .stream()
                .filter(space
                        -> space.getType() == type)
                .filter(space
                        -> space.getStatus()
                == ParkingSpaceStatus.AVAILABLE)
                .findFirst()
                .orElseThrow(()
                        -> new NoCompatibleSpaceException(
                        "No available parking space for type: "
                        + type
                )
                );
    }

    private ParkingTicket
            findActiveTicketByLicensePlate(
                    String licensePlate) {

        String normalizedPlate
                = licensePlate
                        .trim()
                        .toUpperCase();

        return activeTicketsById.values()
                .stream()
                .filter(ticket
                        -> ticket.getVehicle()
                        .getLicensePlate()
                        .equals(
                                normalizedPlate
                        )
                )
                .findFirst()
                .orElse(null);
    }
}
