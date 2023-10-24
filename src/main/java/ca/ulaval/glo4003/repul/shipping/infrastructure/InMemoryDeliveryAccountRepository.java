package ca.ulaval.glo4003.repul.shipping.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryAccount;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryAccountRepository;

public class InMemoryDeliveryAccountRepository implements DeliveryAccountRepository {
    private final Map<UniqueIdentifier, DeliveryAccount> deliveryAccounts = new HashMap<>();

    @Override
    public void saveOrUpdate(DeliveryAccount deliveryAccount) {
        deliveryAccounts.put(deliveryAccount.getAccountId(), deliveryAccount);
    }

    @Override
    public Optional<DeliveryAccount> getByAccountId(UniqueIdentifier accountId) {
        return Optional.ofNullable(deliveryAccounts.get(accountId));
    }

    @Override
    public boolean exists(UniqueIdentifier accountId) {
        return deliveryAccounts.containsKey(accountId);
    }

    @Override
    public List<UniqueIdentifier> getAvailableShippers() {
        return deliveryAccounts.values().stream()
            .map(DeliveryAccount::getAccountId)
            .toList();
    }
}
