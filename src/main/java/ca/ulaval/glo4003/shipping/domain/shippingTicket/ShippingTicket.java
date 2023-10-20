package ca.ulaval.glo4003.shipping.domain.shippingTicket;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;
import ca.ulaval.glo4003.shipping.domain.exception.InvalidShipperException;

public class ShippingTicket {
    private final UniqueIdentifier ticketId;
    private final List<MealkitShippingInfo> mealkitShippingInfos;
    private PickupLocation pickupLocation;
    private UniqueIdentifier shipperId;

    public ShippingTicket(UniqueIdentifier ticketId, PickupLocation pickupLocation, List<MealkitShippingInfo> mealkitShippingInfos) {
        this.ticketId = ticketId;
        this.pickupLocation = pickupLocation;
        this.mealkitShippingInfos = mealkitShippingInfos;
    }

    public UniqueIdentifier getTicketId() {
        return ticketId;
    }

    public UniqueIdentifier getShipperId() {
        return shipperId;
    }

    public PickupLocation getPickupLocation() {
        return pickupLocation;
    }

    public List<MealkitShippingInfo> getMealkitShippingInfos() {
        return mealkitShippingInfos;
    }

    public void pickupCargo(UniqueIdentifier accountId) {
        this.shipperId = accountId;
        mealkitShippingInfos.forEach(MealkitShippingInfo::pickupPendingMealkit);
    }

    public void cancelShipping(UniqueIdentifier accountId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        this.shipperId = null;
        mealkitShippingInfos.forEach(MealkitShippingInfo::cancelInProgressShipping);
    }
}
