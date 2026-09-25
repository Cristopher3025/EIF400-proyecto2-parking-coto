package service;

import java.util.Objects;

import model.parking.ParkingLot;

public final class ParkingContext {

    private final ParkingLot parkingLot;
    private final ExitPaymentService exitPaymentService;
    private final QueryService queryService;

    public ParkingContext(ParkingLot parkingLot) {
        this.parkingLot = Objects.requireNonNull(parkingLot, "Parking lot cannot be null");
        this.exitPaymentService = new ExitPaymentService(parkingLot);
        this.queryService = new QueryService(parkingLot);
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public ExitPaymentService getExitPaymentService() {
        return exitPaymentService;
    }

    public QueryService getQueryService() {
        return queryService;
    }
}