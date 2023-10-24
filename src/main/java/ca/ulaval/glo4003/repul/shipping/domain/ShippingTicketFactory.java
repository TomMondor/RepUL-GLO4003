package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;

public class ShippingTicketFactory {
    public ShippingTicket createShippingTicket(KitchenLocation kitchenLocation, List<MealKitShippingInfo> mealKitShippingInfos) {
        return new ShippingTicket(new UniqueIdentifier(UUID.randomUUID()), kitchenLocation, mealKitShippingInfos);
    }
}
