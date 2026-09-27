package util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong TICKET_SEQUENCE
            = new AtomicLong(0);

    private static final AtomicLong PAYMENT_SEQUENCE
            = new AtomicLong(0);

    private IdGenerator() {
        throw new AssertionError(
                "Utility class cannot be instantiated"
        );
    }

    public static String nextTicketId() {
        return "T-"
                + TICKET_SEQUENCE.incrementAndGet();
    }

    public static String nextPaymentId() {
        return "P-"
                + PAYMENT_SEQUENCE.incrementAndGet();
    }
}
