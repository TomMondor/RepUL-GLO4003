package ca.ulaval.glo4003.repul.delivery.domain;

public interface DeliverySystemRepository {
    void save(DeliverySystem deliverySystem);

    DeliverySystem get();
}
