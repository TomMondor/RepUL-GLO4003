package ca.ulaval.glo4003.repul.cooking.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
import ca.ulaval.glo4003.repul.cooking.domain.exception.KitchenNotFoundException;

public class InMemoryKitchenRepository implements KitchenRepository {
    private Optional<Kitchen> kitchen = Optional.empty();

    @Override
    public void save(Kitchen kitchen) {
        this.kitchen = Optional.of(kitchen);
    }

    @Override
    public Kitchen get() {
        return kitchen.orElseThrow(KitchenNotFoundException::new);
    }
}
