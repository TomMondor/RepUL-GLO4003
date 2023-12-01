package ca.ulaval.glo4003.repul.cooking.domain;

public interface KitchenRepository {
    Kitchen get();

    void save(Kitchen kitchen);
}
