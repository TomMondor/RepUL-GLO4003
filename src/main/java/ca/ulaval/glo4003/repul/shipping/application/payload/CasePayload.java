package ca.ulaval.glo4003.repul.shipping.application.payload;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;

public record CasePayload(String caseId) {
    public static CasePayload from(CaseId caseId) {
        if (caseId == null) {
            return new CasePayload("To be determined");
        } else {
            return new CasePayload(caseId.id());
        }
    }
}
