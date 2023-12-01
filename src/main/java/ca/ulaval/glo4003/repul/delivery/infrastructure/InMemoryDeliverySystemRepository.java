package ca.ulaval.glo4003.repul.delivery.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;

public class InMemoryDeliverySystemRepository implements DeliverySystemRepository {
    private Optional<DeliverySystem> deliverySystem = Optional.empty();

    @Override
    public void save(DeliverySystem deliverySystem) {
        this.deliverySystem = Optional.of(deliverySystem);
    }

    @Override
    public DeliverySystem get() {
        return this.deliverySystem.orElseThrow(DeliverySystemNotFoundException::new);
    }
}
