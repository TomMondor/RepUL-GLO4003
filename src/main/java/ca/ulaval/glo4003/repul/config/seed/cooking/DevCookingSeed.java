package ca.ulaval.glo4003.repul.config.seed.cooking;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.domain.Cook.Cook;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;

public class DevCookingSeed extends CookingSeed {
    private static final CookUniqueIdentifier COOK_ID =
        new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom("f36ec513-6522-404e-a2b9-a4202b12c571");

    public DevCookingSeed(KitchenPersister kitchenPersister) {
        super(kitchenPersister);
    }

    @Override
    public void populate() {
        Kitchen kitchen = new Kitchen(new MealKitFactory());
        createCook(kitchen);
        kitchenPersister.save(kitchen);
    }

    private void createCook(Kitchen kitchen) {
        kitchen.hireCook(new Cook(COOK_ID));
    }
}
