package model.vehicle;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehiclePricingTest {

    private static final Duration TWO_HOURS =
            Duration.ofHours(2);

    @Test
    void shouldCalculateAmountPolymorphicallyAccordingToVehicleType() {

        Vehicle motorcycle =
                new Motorcycle(
                        "M-001",
                        "Honda",
                        "CB500",
                        "Black"
                );

        Vehicle car =
                new Car(
                        "C-001",
                        "Toyota",
                        "Yaris",
                        "Red"
                );

        Vehicle cargoVehicle =
                new CargoVehicle(
                        "T-001",
                        "Isuzu",
                        "NPR",
                        "White"
                );

        assertEquals(
                new BigDecimal("1000"),
                motorcycle.calculateParkingAmount(TWO_HOURS)
        );

        assertEquals(
                new BigDecimal("1800"),
                car.calculateParkingAmount(TWO_HOURS)
        );

        assertEquals(
                new BigDecimal("3000"),
                cargoVehicle.calculateParkingAmount(TWO_HOURS)
        );
    }
}