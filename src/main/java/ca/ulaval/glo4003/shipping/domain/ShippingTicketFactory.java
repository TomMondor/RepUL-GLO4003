package ca.ulaval.glo4003.shipping.domain;

import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;
import ca.ulaval.glo4003.shipping.domain.shippingTicket.MealkitShippingInfo;
import ca.ulaval.glo4003.shipping.domain.shippingTicket.ShippingTicket;

public class ShippingTicketFactory {
    public ShippingTicket createShippingTicket(PickupLocation pickupLocation, List<MealkitShippingInfo> mealkitShippingInfos) {
        return new ShippingTicket(new UniqueIdentifier(UUID.randomUUID()), pickupLocation, mealkitShippingInfos);
    }
}
