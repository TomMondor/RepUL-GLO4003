package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public interface SubscriberRepository {
    void save(Subscriber subscriber);

    List<Subscriber> getAll();

    Subscriber getById(SubscriberUniqueIdentifier subscriberId);

    boolean cardNumberExists(SubscriberCardNumber cardNumber);

    Subscriber findByOrderId(MealKitUniqueIdentifier orderId);
}
