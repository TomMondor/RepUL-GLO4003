package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.Optional;

public interface DeliverySystemRepository {
    void saveOrUpdate(DeliverySystem deliverySystem);

    Optional<DeliverySystem> get();
}
