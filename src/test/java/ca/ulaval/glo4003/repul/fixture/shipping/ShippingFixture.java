package ca.ulaval.glo4003.repul.fixture.shipping;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;

public class ShippingFixture {
    private List<Cargo> cargos;
    private LocationsCatalog locationsCatalog;

    public ShippingFixture() {
        this.cargos = new ArrayList<>();
        this.locationsCatalog = new LocationsCatalogFixture().build();
    }

    public ShippingFixture addCargo(Cargo cargo) {
        this.cargos.add(cargo);
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
