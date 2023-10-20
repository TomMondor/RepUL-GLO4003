package ca.ulaval.glo4003.shipping.domain.shippingTicket;

import ca.ulaval.glo4003.commons.domain.CaseId;
import ca.ulaval.glo4003.commons.domain.MealkitId;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;

public class MealkitShippingInfo {
    private final ShippingLocation shippingLocation;
    private final CaseId caseId;
    private final MealkitId mealkitId;
    private ShippingStatus status;

    public MealkitShippingInfo(ShippingLocation shippingLocation, CaseId caseId, MealkitId mealkitId, ShippingStatus status) {
        this.shippingLocation = shippingLocation;
        this.caseId = caseId;
        this.mealkitId = mealkitId;
        this.status = status;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void pickupPendingMealkit() {
        if (status == ShippingStatus.PENDING) {
            status = ShippingStatus.IN_PROGRESS;
        }
    }

    public void cancelInProgressShipping() {
        if (status == ShippingStatus.IN_PROGRESS) {
            status = ShippingStatus.PENDING;
        }
    }
}
