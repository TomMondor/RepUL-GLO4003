package ca.ulaval.glo4003.commons.domain;

import ca.ulaval.glo4003.commons.domain.exception.InvalidCaseIdException;

public record CaseId(String value) {
    public CaseId {
        if (value == null || value.isBlank()) {
            throw new InvalidCaseIdException();
        }
    }
}
