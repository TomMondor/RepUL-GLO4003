package ca.ulaval.glo4003.repul.config;

import java.time.Duration;
import java.time.LocalTime;

public class Config {
    private static Config instance;
    private final LocalTime openingTime;
    private final Duration durationToConfirm;

    private Config(LocalTime openingTime, Duration durationToConfirm) {
        this.openingTime = openingTime;
        this.durationToConfirm = durationToConfirm;
    }

    public static synchronized Config getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Config has not been initialized.");
        }
        return instance;
    }

    public static synchronized void initialize(LocalTime openingTime, Duration durationToConfirm) {
        if (instance == null) {
            instance = new Config(openingTime, durationToConfirm);
        }
    }

    public LocalTime getOpeningTime() {
        return instance.openingTime;
    }

    public Duration getDurationToConfirm() {
        return instance.durationToConfirm;
    }
}
