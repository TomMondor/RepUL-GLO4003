package ca.ulaval.glo4003.repul.config.seed.cooking;

import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;

public abstract class CookingSeed implements Seed {
    protected final KitchenPersister kitchenPersister;

    protected CookingSeed(KitchenPersister kitchenPersister) {
        this.kitchenPersister = kitchenPersister;
    }
}
