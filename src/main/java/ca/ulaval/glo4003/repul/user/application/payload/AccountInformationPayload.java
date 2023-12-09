package ca.ulaval.glo4003.repul.user.application.payload;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.domain.account.Name;

public record AccountInformationPayload(
    Name name,
    Birthdate birthdate,
    Gender gender,
    int age,
    IDUL idul,
    Email email
) {
    public static AccountInformationPayload fromAccount(Account account) {
        return new AccountInformationPayload(
            account.getName(),
            account.getBirthdate(),
            account.getGender(),
            account.getBirthdate().getAge(),
            account.getIdul(),
            account.getEmail()
        );
    }
}
