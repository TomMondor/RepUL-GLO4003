package ca.ulaval.glo4003.repul.cooking.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;

public class InMemoryKitchenRepository implements KitchenRepository {
    private Optional<Kitchen> kitchen = Optional.empty();

    @Override
    public void saveOrUpdate(Kitchen kitchen) {
        this.kitchen = Optional.of(kitchen);
    }

    @Override
    public Optional<Kitchen> get() {
        return kitchen;
    }
}
