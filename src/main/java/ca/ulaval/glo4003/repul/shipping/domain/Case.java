package ca.ulaval.glo4003.repul.shipping.domain;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class Case {
    private final CaseId caseId;
    private UniqueIdentifier mealKitId;

    public Case(CaseId caseId) {
        this.caseId = caseId;
    }

    public boolean isAssigned() {
        return this.mealKitId != null;
    }

    public boolean isUnassigned() {
        return this.mealKitId == null;
    }

    public void assignCase(UniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
    }

    public void unassignCase() {
        this.mealKitId = null;
    }

    public CaseId getCaseId() {
        return caseId;
    }
}
