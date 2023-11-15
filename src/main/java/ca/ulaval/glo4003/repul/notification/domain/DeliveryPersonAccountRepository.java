package ca.ulaval.glo4003.repul.notification.domain;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface DeliveryPersonAccountRepository {
    void saveOrUpdate(DeliveryPersonAccount account);

    Optional<DeliveryPersonAccount> getByAccountId(UniqueIdentifier accountId);
}
