package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShippingIdException;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfoFactory;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;

public class Shipping {
    private final Map<UniqueIdentifier, MealKitShippingInfo> mealKitShippingInfos = new HashMap<>();
    private final List<ShippingTicket> shippingTickets = new ArrayList<>();
    private final LocationsCatalog locationsCatalog;
    private final ShippingTicketFactory shippingTicketFactory;
    private final MealKitShippingInfoFactory mealKitShippingInfoFactory;
    private final List<UniqueIdentifier> deliveryAccountIds = new ArrayList<>();

    public Shipping(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.shippingTicketFactory = new ShippingTicketFactory();
        this.mealKitShippingInfoFactory = new MealKitShippingInfoFactory();
    }

    public void createMealKitShippingInfo(DeliveryLocationId deliveryLocationId, UniqueIdentifier mealKitId) {
        mealKitShippingInfos.put(mealKitId,
            mealKitShippingInfoFactory.createMealKitShippingInfo(locationsCatalog.getDeliveryLocation(deliveryLocationId), mealKitId));
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
            locationsCatalog.getKitchenLocation(kitchenLocationId),
            mealKitShippingInfos
        );

        shippingTickets.add(shippingTicket);

        return shippingTicket;
    }

    public MealKitShippingInfo getMealKitShippingInfo(UniqueIdentifier mealKitId) {
        return mealKitShippingInfos.get(mealKitId);
    }

    public List<DeliveryLocation> getShippingLocations() {
        return locationsCatalog.getDeliveryLocations();
    }

    public List<MealKitShippingInfo> pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        validateDeliveryPersonExists(accountId);
        return shippingTickets.stream()
            .filter(shippingTicket -> shippingTicket.getTicketId().equals(shippingId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .pickupCargo(accountId);
    }

    private void validateDeliveryPersonExists(UniqueIdentifier accountId) {
        if (!deliveryAccountIds.contains(accountId)) {
            throw new DeliveryPersonNotFoundException();
        }
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

    public void addDeliveryPerson(UniqueIdentifier accountId) {
        deliveryAccountIds.add(accountId);
    }

    public List<UniqueIdentifier> getDeliveryPeople() {
        return deliveryAccountIds;
    }
}
