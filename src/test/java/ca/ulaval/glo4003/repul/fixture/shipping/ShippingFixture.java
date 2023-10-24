package ca.ulaval.glo4003.repul.fixture.shipping;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;

public class ShippingFixture {
    private List<ShippingTicket> shippingTickets;
    private LocationsCatalog locationsCatalog;

    public ShippingFixture() {
        this.shippingTickets = new ArrayList<>();
        this.locationsCatalog = new LocationsCatalogFixture().build();
    }

    public ShippingFixture addShippingTicket(ShippingTicket shippingTicket) {
        this.shippingTickets.add(shippingTicket);
        return this;
    }

    public ShippingFixture withLocationsCatalog(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        return this;
    }

    public Shipping build() {
        Shipping shipping = new Shipping(locationsCatalog);
        return shipping;
    }
}
