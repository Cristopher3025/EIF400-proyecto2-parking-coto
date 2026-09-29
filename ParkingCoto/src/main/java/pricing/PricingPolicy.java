package pricing;

import java.math.BigDecimal;
import java.time.Duration;

public interface PricingPolicy {

    BigDecimal calculateAmount(Duration duration);
}
