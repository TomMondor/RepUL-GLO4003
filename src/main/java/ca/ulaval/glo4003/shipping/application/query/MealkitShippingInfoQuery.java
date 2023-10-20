package ca.ulaval.glo4003.shipping.application.query;

import ca.ulaval.glo4003.commons.domain.CaseId;
import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.commons.domain.MealkitId;

public record MealkitShippingInfoQuery(LocationId shippingLocationId, CaseId caseId, MealkitId mealkitId) {
    public MealkitShippingInfoQuery(String shippingLocationId, String caseId, String mealkitId) {
        this(new LocationId(shippingLocationId), new CaseId(caseId), new MealkitId(mealkitId));
    }

    public static MealkitShippingInfoQuery from(String shippingLocationId, String caseId, String mealkitId) {
        return new MealkitShippingInfoQuery(shippingLocationId, caseId, mealkitId);
    }
}
