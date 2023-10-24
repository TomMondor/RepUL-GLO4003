package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface DeliveryAccountRepository {
    void saveOrUpdate(DeliveryAccount deliveryAccount);

    Optional<DeliveryAccount> getByAccountId(UniqueIdentifier accountId);

    boolean exists(UniqueIdentifier accountId);

    List<UniqueIdentifier> getAvailableShippers();
}
