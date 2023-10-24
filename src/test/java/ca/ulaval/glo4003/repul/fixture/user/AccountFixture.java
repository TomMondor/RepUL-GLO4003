package ca.ulaval.glo4003.repul.fixture.user;

import java.time.LocalDate;
import java.util.UUID;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.domain.account.Name;

public class AccountFixture {
    private UniqueIdentifier uid;
    private Name name;
    private Birthdate birthDate;
    private Gender gender;
    private IDUL idul;
    private Email email;

    public AccountFixture() {
        uid = new UniqueIdentifier(UUID.randomUUID());
        name = new Name("John Doe");
        birthDate = new Birthdate(LocalDate.now().minusYears(23));
        gender = Gender.UNDISCLOSED;
        idul = new IDUL("ALMAT69");
        email = new Email("test.jdoe@ulaval.ca");
    }

    public AccountFixture withAccountId(UniqueIdentifier uid) {
        this.uid = uid;
        return this;
    }

    public AccountFixture withName(Name name) {
        this.name = name;
        return this;
    }

    public AccountFixture withBirthDate(Birthdate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public AccountFixture withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public AccountFixture withIDUL(IDUL idul) {
        this.idul = idul;
        return this;
    }

    public AccountFixture withEmail(Email email) {
        this.email = email;
        return this;
    }

    public Account build() {
        Account account = new Account(uid, idul, name, birthDate, gender, email);
        return account;
    }
}
