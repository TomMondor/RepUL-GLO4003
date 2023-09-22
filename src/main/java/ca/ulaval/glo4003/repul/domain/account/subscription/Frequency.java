package ca.ulaval.glo4003.repul.domain.account.subscription;

import ca.ulaval.glo4003.repul.domain.exception.InvalidFrequencyException;

public record Frequency(int value) {
    private static final int DEFAULT_FREQUENCY = 7;

    public Frequency {
        if (value <= 0) {
            throw new InvalidFrequencyException();
        }
    }

    public Frequency() {
        this(DEFAULT_FREQUENCY);
    }
}
