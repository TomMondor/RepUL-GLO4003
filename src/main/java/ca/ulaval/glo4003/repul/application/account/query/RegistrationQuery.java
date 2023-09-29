package ca.ulaval.glo4003.repul.application.account.query;

import java.time.LocalDate;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.account.Gender;
import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.account.Name;

public record RegistrationQuery(IDUL idul, Email email, String password, Name name, Birthdate birthdate, Gender gender) {
    public static RegistrationQuery from(String idul, String email, String password, String name, String birthdate, String gender) {
        return new RegistrationQuery(new IDUL(idul), new Email(email), password, new Name(name), new Birthdate(LocalDate.parse(birthdate)),
            Gender.from(gender));
    }
}
