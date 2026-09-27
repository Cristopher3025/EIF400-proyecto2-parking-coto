package pricing;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HourlyRateWithDailyCapTest {

    private final HourlyRateWithDailyCap carPricing =
            new HourlyRateWithDailyCap(
                    new BigDecimal("900"),
                    new BigDecimal("7000")
            );

    @Test
    void shouldChargeOneHourForOneMinute() {

        assertEquals(
                new BigDecimal("900"),
                carPricing.calculateAmount(
                        Duration.ofMinutes(1)
                )
        );
    }

    @Test
    void shouldChargeOneHourForThirtyFiveMinutes() {

        assertEquals(
                new BigDecimal("900"),
                carPricing.calculateAmount(
                        Duration.ofMinutes(35)
                )
        );
    }

    @Test
    void shouldChargeOneHourForExactlySixtyMinutes() {

        assertEquals(
                new BigDecimal("900"),
                carPricing.calculateAmount(
                        Duration.ofMinutes(60)
                )
        );
    }

    @Test
    void shouldChargeTwoHoursForSixtyOneMinutes() {

        assertEquals(
                new BigDecimal("1800"),
                carPricing.calculateAmount(
                        Duration.ofMinutes(61)
                )
        );
    }

    @Test
    void shouldChargeThreeHoursForTwoHoursAndOneMinute() {

        assertEquals(
                new BigDecimal("2700"),
                carPricing.calculateAmount(
                        Duration.ofMinutes(121)
                )
        );
    }

    @Test
    void shouldChargeThreeHoursForExactlyThreeHours() {

        assertEquals(
                new BigDecimal("2700"),
                carPricing.calculateAmount(
                        Duration.ofHours(3)
                )
        );
    }

    @Test
    void shouldChargeRegularRateBeforeTenHours() {

        assertEquals(
                new BigDecimal("8100"),
                carPricing.calculateAmount(
                        Duration.ofHours(9)
                )
        );
    }

    @Test
    void shouldApplyDailyCapAtTenHours() {

        assertEquals(
                new BigDecimal("7000"),
                carPricing.calculateAmount(
                        Duration.ofHours(10)
                )
        );
    }

    @Test
    void shouldApplyDailyCapForTwentyFourHours() {

        assertEquals(
                new BigDecimal("7000"),
                carPricing.calculateAmount(
                        Duration.ofHours(24)
                )
        );
    }

    @Test
    void shouldChargeDailyCapPlusOneHourForTwentyFiveHours() {

        assertEquals(
                new BigDecimal("7900"),
                carPricing.calculateAmount(
                        Duration.ofHours(25)
                )
        );
    }

    @Test
    void shouldChargeDailyCapPlusSixHoursForThirtyHours() {

        assertEquals(
                new BigDecimal("12400"),
                carPricing.calculateAmount(
                        Duration.ofHours(30)
                )
        );
    }

    @Test
    void shouldApplyTwoDailyCapsForFortyEightHours() {

        assertEquals(
                new BigDecimal("14000"),
                carPricing.calculateAmount(
                        Duration.ofHours(48)
                )
        );
    }

    @Test
    void shouldChargeOneHourForZeroDuration() {

        assertEquals(
                new BigDecimal("900"),
                carPricing.calculateAmount(
                        Duration.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeDuration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> carPricing.calculateAmount(
                        Duration.ofMinutes(-1)
                )
        );
    }

    @Test
    void shouldRejectZeroHourlyRate() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new HourlyRateWithDailyCap(
                        BigDecimal.ZERO,
                        new BigDecimal("7000")
                )
        );
    }

    @Test
    void shouldRejectZeroDailyCap() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new HourlyRateWithDailyCap(
                        new BigDecimal("900"),
                        BigDecimal.ZERO
                )
        );
    }
}