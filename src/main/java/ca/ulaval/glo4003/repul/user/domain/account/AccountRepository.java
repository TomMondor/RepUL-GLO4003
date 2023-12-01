package ca.ulaval.glo4003.repul.user.domain.account;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface AccountRepository {
    void save(Account account);

    boolean exists(IDUL idul);

    Account getByAccountId(UniqueIdentifier accountId);

    boolean isUserCardNumberUsed(UserCardNumber cardNumber);
}
