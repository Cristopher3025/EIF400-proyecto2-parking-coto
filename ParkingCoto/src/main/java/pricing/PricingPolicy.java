package pricing;

import java.math.BigDecimal;
import java.time.Duration;

/**
 *
 * @author Justin PC
 */
public interface PricingPolicy {

    BigDecimal calculateAmount(Duration duration);
}
