package ca.ulaval.glo4003.repul.application.account.payload;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.account.Gender;
import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.account.Name;

public record AccountInformationPayload(Name name, Birthdate birthdate, Gender gender, int age, IDUL idul, Email email) {
    public static AccountInformationPayload fromAccount(Account account) {
        return new AccountInformationPayload(account.getName(), account.getbirthdate(), account.getGender(), account.getbirthdate().getAge(), account.getIdul(),
            account.getEmail());
    }
}
