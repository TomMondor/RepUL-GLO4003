package ca.ulaval.glo4003.repul.fixture.shipping;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;

public class ShippingFixture {
    private List<ShippingTicket> shippingTickets;
    private ShippingCatalog shippingCatalog;

    public ShippingFixture() {
        this.shippingTickets = new ArrayList<>();
        this.shippingCatalog = new ShippingCatalogFixture().build();
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
        Shipping shipping = new Shipping(shippingCatalog);
        return shipping;
    }
}
