package service;

import java.util.Objects;
import model.parking.ParkingLot;

public final class ParkingContext {

    private final RegistrationService registrationService;
    private final EntryService entryService;
    private final ExitService exitService;
    private final PaymentService paymentService;
    private final QueryService queryService;

    public ParkingContext(
            ParkingLot parkingLot) {

        Objects.requireNonNull(
                parkingLot,
                "Parking lot cannot be null"
        );

        this.registrationService =
                new RegistrationService(parkingLot);

        this.entryService =
                new EntryService(parkingLot);

        this.exitService =
                new ExitService(parkingLot);

        this.paymentService =
                new PaymentService(parkingLot);

        this.queryService =
                new QueryService(parkingLot);
    }

    public RegistrationService
            getRegistrationService() {

        return registrationService;
    }

    public EntryService getEntryService() {
        return entryService;
    }

    public ExitService getExitService() {
        return exitService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public QueryService getQueryService() {
        return queryService;
    }
}