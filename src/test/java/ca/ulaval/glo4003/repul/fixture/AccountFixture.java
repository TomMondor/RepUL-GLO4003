package ca.ulaval.glo4003.repul.fixture;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.account.Gender;
import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.account.Name;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;

public class AccountFixture {
    private UniqueIdentifier uid;
    private Name name;
    private Birthdate birthDate;
    private Gender gender;
    private IDUL idul;
    private Email email;
    private List<Subscription> subscriptions = new ArrayList<>();

    public AccountFixture() {
        uid = new UniqueIdentifier(UUID.randomUUID());
        name = new Name("John Doe");
        birthDate = new Birthdate(LocalDate.now().minusYears(23));
        gender = Gender.UNDISCLOSED;
        idul = new IDUL("thmon420");
        email = new Email("test@jdoe.com");
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

    public AccountFixture withSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    public AccountFixture addSubscription(Subscription subscription) {
        this.subscriptions.add(subscription);
        return this;
    }

    public Account build() {
        Account account = new Account(uid, name, birthDate, gender, idul, email);
        subscriptions.forEach(account::addSubscription);
        return account;
    }
}
