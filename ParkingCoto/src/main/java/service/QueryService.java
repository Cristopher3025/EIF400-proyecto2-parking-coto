package service;

import enums.ParkingSpaceStatus;
import enums.ParkingSpaceType;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import model.parking.ParkingLot;
import model.parking.ParkingSpace;
import model.payment.Payment;
import model.ticket.ParkingTicket;
import model.vehicle.Vehicle;

public class QueryService {

    private final ParkingLot parkingLot;

    public QueryService(
            ParkingLot parkingLot) {

        this.parkingLot =
                Objects.requireNonNull(
                        parkingLot,
                        "Parking lot cannot be null"
                );
    }

    public Vehicle findVehicle(
            String licensePlate) {

        return parkingLot.findVehicle(
                licensePlate
        );
    }

    public ParkingTicket findTicket(
            String ticketId) {

        return parkingLot.findTicket(
                ticketId
        );
    }

    public ParkingTicket findActiveTicket(
            String ticketId) {

        return parkingLot.findActiveTicket(
                ticketId
        );
    }

    public List<Vehicle> getVehicles() {

        return parkingLot.getVehicles();
    }

    public List<ParkingSpace>
            getParkingSpaces() {

        return parkingLot.getParkingSpaces();
    }

    public List<ParkingTicket>
            getActiveTickets() {

        return parkingLot.getActiveTickets();
    }

    public List<ParkingTicket>
            getTickets() {

        return parkingLot.getTickets();
    }

    public List<Payment>
            getPayments() {

        return parkingLot.getPayments();
    }

    public List<ParkingSpace>
            getAvailableParkingSpaces() {

        return parkingLot.getParkingSpaces()
                .stream()

                .filter(space ->
                        space.getStatus()
                        == ParkingSpaceStatus.AVAILABLE)

                .collect(Collectors.toList());
    }

    public List<ParkingSpace>
            getOccupiedParkingSpaces() {

        return parkingLot.getParkingSpaces()
                .stream()

                .filter(space ->
                        space.getStatus()
                        == ParkingSpaceStatus.OCCUPIED)

                .collect(Collectors.toList());
    }

    public List<Vehicle>
            getVehiclesCurrentlyParked() {

        return parkingLot.getActiveTickets()
                .stream()

                .map(ParkingTicket::getVehicle)

                .collect(Collectors.toList());
    }

    public Map<ParkingSpaceType, Long>
            getOccupancyByType() {

        Map<ParkingSpaceType, Long> occupancy =
                new EnumMap<>(
                        ParkingSpaceType.class
                );

        for (ParkingSpaceType type
                : ParkingSpaceType.values()) {

            long occupied =
                    parkingLot.getParkingSpaces()
                            .stream()

                            .filter(space ->
                                    space.getType()
                                    == type)

                            .filter(space ->
                                    space.getStatus()
                                    == ParkingSpaceStatus.OCCUPIED)

                            .count();

            occupancy.put(
                    type,
                    occupied
            );
        }

        return occupancy;
    }

    public BigDecimal getTotalRevenue() {

        return parkingLot.getPayments()
                .stream()

                .map(Payment::getAmount)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public long getRegisteredVehicleCount() {
        return parkingLot
                .getVehicles()
                .size();
    }

    public long getAvailableSpaceCount() {
        return getAvailableParkingSpaces()
                .size();
    }

    public long getOccupiedSpaceCount() {
        return getOccupiedParkingSpaces()
                .size();
    }

    public long getActiveTicketCount() {
        return parkingLot
                .getActiveTickets()
                .size();
    }
}