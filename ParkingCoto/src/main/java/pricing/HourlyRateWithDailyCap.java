package pricing;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

public class HourlyRateWithDailyCap
        implements PricingPolicy {

    private static final long MINUTES_PER_HOUR = 60;
    private static final long HOURS_PER_DAY = 24;
    private static final long HOURS_FOR_DAILY_CAP = 10;

    private final BigDecimal hourlyRate;
    private final BigDecimal dailyCap;

    public HourlyRateWithDailyCap(
            BigDecimal hourlyRate,
            BigDecimal dailyCap) {

        this.hourlyRate =
                validatePositiveAmount(
                        hourlyRate,
                        "Hourly rate"
                );

        this.dailyCap =
                validatePositiveAmount(
                        dailyCap,
                        "Daily cap"
                );
    }

    @Override
    public BigDecimal calculateAmount(
            Duration duration) {

        validateDuration(duration);

        long chargedHours =
                calculateChargedHours(duration);

        long completeDays =
                chargedHours / HOURS_PER_DAY;

        long remainingHours =
                chargedHours % HOURS_PER_DAY;

        BigDecimal total =
                dailyCap.multiply(
                        BigDecimal.valueOf(
                                completeDays
                        )
                );

        if (remainingHours > 0) {
            total = total.add(
                    calculatePeriodAmount(
                            remainingHours
                    )
            );
        }

        return total;
    }

    public long calculateChargedHours(
            Duration duration) {

        validateDuration(duration);

        long minutes = duration.toMinutes();

        if (minutes <= 0) {
            return 1;
        }

        return (minutes + MINUTES_PER_HOUR - 1)
                / MINUTES_PER_HOUR;
    }

    private BigDecimal calculatePeriodAmount(
            long hours) {

        BigDecimal regularAmount =
                hourlyRate.multiply(
                        BigDecimal.valueOf(hours)
                );

        if (hours >= HOURS_FOR_DAILY_CAP) {
            return regularAmount.min(dailyCap);
        }

        return regularAmount;
    }

    private void validateDuration(
            Duration duration) {

        Objects.requireNonNull(
                duration,
                "Duration cannot be null"
        );

        if (duration.isNegative()) {
            throw new IllegalArgumentException(
                    "Duration cannot be negative"
            );
        }
    }

    private BigDecimal validatePositiveAmount(
            BigDecimal amount,
            String fieldName) {

        Objects.requireNonNull(
                amount,
                fieldName + " cannot be null"
        );

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                    + " must be greater than zero"
            );
        }

        return amount;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public BigDecimal getDailyCap() {
        return dailyCap;
    }
}