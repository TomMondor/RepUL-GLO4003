package ca.ulaval.glo4003.repul.user.domain.account;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class AccountFactory {
    public Account createAccount(UniqueIdentifier accountId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        return new Account(accountId, idul, name, birthdate, gender, email);
    }
}
