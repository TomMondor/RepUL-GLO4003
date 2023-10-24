package ca.ulaval.glo4003.repul.small.shipping.application.payload;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.shipping.application.payload.CasePayload;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CasePayloadTest {
    private static final String CASE_ID = "a case id";

    @Test
    public void givenCaseId_whenUsingFrom_shouldReturnCorrectCasePayload() {
        CasePayload expectedCasePayload = new CasePayload(CASE_ID);
        CaseId caseId = new CaseId(CASE_ID,1);

        CasePayload actualCasePayload = CasePayload.from(caseId);

        assertEquals(expectedCasePayload, actualCasePayload);
    }

    @Test
    public void givenNullCaseId_whenUsingFrom_shouldReturnCorrectCasePayload() {
        CasePayload expectedCasePayload = new CasePayload("To be determined");

        CasePayload actualCasePayload = CasePayload.from(null);

        assertEquals(expectedCasePayload, actualCasePayload);
    }
}
