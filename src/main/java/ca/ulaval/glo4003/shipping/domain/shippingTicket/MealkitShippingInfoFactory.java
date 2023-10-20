package ca.ulaval.glo4003.shipping.domain.shippingTicket;

import ca.ulaval.glo4003.commons.domain.CaseId;
import ca.ulaval.glo4003.commons.domain.MealkitId;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

public class MealkitShippingInfoFactory {
    public MealkitShippingInfo createMealkitShippingInfo(ShippingLocation shippingLocation, CaseId caseId, MealkitId mealkitId) {
        return new MealkitShippingInfo(shippingLocation, caseId, mealkitId, ShippingStatus.PENDING);
    }
}
