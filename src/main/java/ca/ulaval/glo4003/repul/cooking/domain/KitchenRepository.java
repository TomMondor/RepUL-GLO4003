package ca.ulaval.glo4003.repul.cooking.domain;

import java.util.Optional;

public interface KitchenRepository {
    Optional<Kitchen> get();

    void save(Kitchen kitchen);
}
