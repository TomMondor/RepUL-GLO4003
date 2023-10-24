package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.Optional;

public interface ShippingRepository {
    void saveOrUpdate(Shipping shipping);

    Optional<Shipping> get();
}
