package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidCaseIdException;

public record CaseId(String id, int caseNumber) {
    public CaseId {
        if (id == null || id.isBlank() || caseNumber <= 0) {
            throw new InvalidCaseIdException();
        }
    }
}
