package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShippingIdException;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfoFactory;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;

public class Shipping {
    private final Map<UniqueIdentifier, MealKitShippingInfo> mealKitShippingInfos = new HashMap<>();
    private final List<ShippingTicket> shippingTickets = new ArrayList<>();
    private final ShippingCatalog shippingCatalog;
    private final ShippingTicketFactory shippingTicketFactory;
    private final MealKitShippingInfoFactory mealKitShippingInfoFactory;

    public Shipping(ShippingCatalog shippingCatalog) {
        this.shippingCatalog = shippingCatalog;
        this.shippingTicketFactory = new ShippingTicketFactory();
        this.mealKitShippingInfoFactory = new MealKitShippingInfoFactory();
    }

    public void createMealKitShippingInfo(DeliveryLocationId deliveryLocationId, UniqueIdentifier mealKitId) {
        mealKitShippingInfos.put(mealKitId,
            mealKitShippingInfoFactory.createMealKitShippingInfo(shippingCatalog.getDeliveryLocation(deliveryLocationId), mealKitId));
    }

    public List<ShippingTicket> getShippingTickets() {
        return shippingTickets;
    }

    public ShippingTicket receiveReadyToBeDeliveredMealKit(KitchenLocationId kitchenLocationId, List<UniqueIdentifier> mealKitIds) {
        List<MealKitShippingInfo> mealKitShippingInfos = mealKitIds.stream()
            .map(this.mealKitShippingInfos::remove)
            .toList();

        mealKitShippingInfos.forEach(MealKitShippingInfo::assignCase);

        mealKitShippingInfos.forEach(MealKitShippingInfo::markAsReadyToBeDelivered);

        ShippingTicket shippingTicket = shippingTicketFactory.createShippingTicket(
            shippingCatalog.getKitchenLocation(kitchenLocationId),
            mealKitShippingInfos
        );

        shippingTickets.add(shippingTicket);

        return shippingTicket;
    }

    public MealKitShippingInfo getMealKitShippingInfo(UniqueIdentifier mealKitId) {
        return mealKitShippingInfos.get(mealKitId);
    }

    public List<DeliveryLocation> getShippingLocations() {
        return shippingCatalog.getDeliveryLocations();
    }

    public List<MealKitShippingInfo> pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        return shippingTickets.stream()
            .filter(shippingTicket -> shippingTicket.getTicketId().equals(shippingId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .pickupCargo(accountId);
    }

    public void cancelShipping(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        shippingTickets.stream()
            .filter(shippingTicket -> shippingTicket.getTicketId().equals(shippingId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .cancelShipping(accountId);
    }

    public void confirmShipping(UniqueIdentifier accountId, UniqueIdentifier ticketId, UniqueIdentifier mealKitId) {
        shippingTickets.stream()
            .filter(shippingTicket -> shippingTicket.getTicketId().equals(ticketId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .confirmShipping(accountId, mealKitId);
    }

    public CaseId unconfirmShipping(UniqueIdentifier accountId, UniqueIdentifier ticketId, UniqueIdentifier mealKitId) {
        return shippingTickets.stream()
            .filter(shippingTicket -> shippingTicket.getTicketId().equals(ticketId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .unconfirmShipping(accountId, mealKitId);
    }
}
