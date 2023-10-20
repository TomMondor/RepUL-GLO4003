package ca.ulaval.glo4003.shipping.domain;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.application.query.MealkitShippingInfoQuery;
import ca.ulaval.glo4003.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;
import ca.ulaval.glo4003.shipping.domain.exception.InvalidShippingIdException;
import ca.ulaval.glo4003.shipping.domain.shippingTicket.MealkitShippingInfoFactory;
import ca.ulaval.glo4003.shipping.domain.shippingTicket.ShippingTicket;

public class Shipping {
    private final List<ShippingTicket> shippingTickets;
    private final ShippingCatalog shippingCatalog;
    private final ShippingTicketFactory shippingTicketFactory;
    private final MealkitShippingInfoFactory mealkitShippingInfoFactory;

    public Shipping(List<ShippingTicket> shippingTickets, ShippingCatalog shippingCatalog) {
        this.shippingTickets = shippingTickets;
        this.shippingCatalog = shippingCatalog;
        this.shippingTicketFactory = new ShippingTicketFactory();
        this.mealkitShippingInfoFactory = new MealkitShippingInfoFactory();
    }

    public void createShippingTicket(LocationId pickupLocation, List<MealkitShippingInfoQuery> mealkitShippingInfos) {
        shippingTickets.add(shippingTicketFactory.createShippingTicket(
            shippingCatalog.getPickupLocation(pickupLocation),
            mealkitShippingInfos.stream()
                .map(mealkit -> mealkitShippingInfoFactory.createMealkitShippingInfo(
                    shippingCatalog.getShippingLocation(mealkit.shippingLocationId()),
                    mealkit.caseId(),
                    mealkit.mealkitId()
                )).toList()
        ));
    }

    public List<ShippingTicket> getShippingTickets() {
        return shippingTickets;
    }

    public List<ShippingLocation> getShippingLocations() {
        return shippingCatalog.getShippingLocations();
    }

    public void pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        shippingTickets.stream()
            .filter(t -> t.getTicketId().equals(shippingId))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .pickupCargo(accountId);
    }

    public void cancelShipping(UniqueIdentifier accountId, UniqueIdentifier from) {
        shippingTickets.stream()
            .filter(t -> t.getTicketId().equals(from))
            .findFirst()
            .orElseThrow(InvalidShippingIdException::new)
            .cancelShipping(accountId);
    }
}
