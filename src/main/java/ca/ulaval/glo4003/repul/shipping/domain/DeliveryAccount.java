package ca.ulaval.glo4003.repul.shipping.domain;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class DeliveryAccount {
    private final UniqueIdentifier accountId;

    public DeliveryAccount(UniqueIdentifier accountId) {
        this.accountId = accountId;
    }

    public UniqueIdentifier getAccountId() {
        return accountId;
    }
}
