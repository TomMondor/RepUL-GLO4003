package ca.ulaval.glo4003.shipping.fixture;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.shipping.domain.Shipping;
import ca.ulaval.glo4003.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.shipping.domain.shippingTicket.ShippingTicket;

public class ShippingFixture {
    private List<ShippingTicket> shippingTickets;
    private ShippingCatalog shippingCatalog;

    public ShippingFixture() {
        this.shippingTickets = new ArrayList<>();
        this.shippingCatalog = new ShippingCatalogFixture().build();
    }

    public ShippingFixture withShippingTickets(List<ShippingTicket> shippingTickets) {
        this.shippingTickets = shippingTickets;
        return this;
    }

    public ShippingFixture addShippingTicket(ShippingTicket shippingTicket) {
        this.shippingTickets.add(shippingTicket);
        return this;
    }

    public ShippingFixture withShippingCatalog(ShippingCatalog shippingCatalog) {
        this.shippingCatalog = shippingCatalog;
        return this;
    }

    public Shipping build() {
        Shipping shipping = new Shipping(shippingTickets, shippingCatalog);
        return shipping;
    }
}
