package ca.ulaval.glo4003.repul.cooking.domain.Cook;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidCookIdException;

public class Cooks {
    Map<CookUniqueIdentifier, Cook> cooks;

    public Cooks() {
        cooks = new HashMap<>();
    }

    public Cook get(CookUniqueIdentifier cookId) {
        if (!cooks.containsKey(cookId)) {
            throw new InvalidCookIdException();
        }
        return cooks.get(cookId);
    }

    public void add(Cook cook) {
        cooks.put(cook.getId(), cook);
    }
}
