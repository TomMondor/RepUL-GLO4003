package ca.ulaval.glo4003.repul.domain.account;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;

public class AccountFactory {
    public Account createAccount(UniqueIdentifier accountId, Name name, Birthdate birthdate, Gender gender, IDUL idul, Email email) {
        return new Account(accountId, name, birthdate, gender, idul, email);
    }
}
