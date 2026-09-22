package pricing;

import java.math.BigDecimal;

/**
 *
 * @author Justin PC
 */
public final class ParkingCotoRates {

    private ParkingCotoRates() {
    }

    public static PricingPolicy motorcycle() {
        return new HourlyRateWithDailyCap(
                new BigDecimal("500"),
                new BigDecimal("4000")
        );
    }

    public static PricingPolicy car() {
        return new HourlyRateWithDailyCap(
                new BigDecimal("900"),
                new BigDecimal("7000")
        );
    }

    public static PricingPolicy cargoVehicle() {
        return new HourlyRateWithDailyCap(
                new BigDecimal("1500"),
                new BigDecimal("11000")
        );
    }

}
