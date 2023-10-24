package ca.ulaval.glo4003.repul.fixture.user;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.user.application.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.user.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.user.domain.account.Gender;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;
import ca.ulaval.glo4003.repul.user.domain.account.Name;

public class AccountInformationPayloadFixture {
    private Name name = new Name("John Doe");
    private Birthdate birthdate = new Birthdate(LocalDate.parse("1999-01-01"));
    private Gender gender = Gender.MAN;
    private int age = 22;
    private IDUL idul = new IDUL("ALMAT69");
    private Email email = new Email("anEmail@ulaval.ca");

    public AccountInformationPayloadFixture withName(Name name) {
        this.name = name;
        return this;
    }

    public AccountInformationPayloadFixture withBirthdate(Birthdate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public AccountInformationPayloadFixture withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public AccountInformationPayloadFixture withAge(int age) {
        this.age = age;
        return this;
    }

    public AccountInformationPayloadFixture withIdul(IDUL idul) {
        this.idul = idul;
        return this;
    }

    public AccountInformationPayloadFixture withEmail(Email email) {
        this.email = email;
        return this;
    }

    public AccountInformationPayload build() {
        return new AccountInformationPayload(name, birthdate, gender, age, idul, email);
    }
}
