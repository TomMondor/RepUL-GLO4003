package ca.ulaval.glo4003.repul.shipping.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;

public class InMemoryShippingRepository implements ShippingRepository {
    private Optional<Shipping> shipping = Optional.empty();

    @Override
    public void saveOrUpdate(Shipping shipping) {
        this.shipping = Optional.of(shipping);
    }

    @Override
    public Optional<Shipping> get() {
        return shipping;
    }
}
