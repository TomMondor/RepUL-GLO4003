package ca.ulaval.glo4003.repul.notification.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;

public class InMemoryDeliveryPersonAccountRepository implements DeliveryPersonAccountRepository {
    private final Map<UniqueIdentifier, DeliveryPersonAccount> accounts = new HashMap<>();

    @Override
    public void saveOrUpdate(DeliveryPersonAccount account) {
        accounts.put(account.getAccountId(), account);
    }

    @Override
    public Optional<DeliveryPersonAccount> getByAccountId(UniqueIdentifier accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
