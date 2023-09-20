package ca.ulaval.glo4003.repul.domain.catalog;

public record LocationId(String value) {
    public LocationId {
        if (value == null) {
            throw new IllegalArgumentException("Location Id cannot be null");
        } else if (value.isBlank()) {
            throw new IllegalArgumentException("Location Id cannot be blank");
        }
    }
}
