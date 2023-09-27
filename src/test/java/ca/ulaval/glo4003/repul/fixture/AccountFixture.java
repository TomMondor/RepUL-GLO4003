package ca.ulaval.glo4003.repul.fixture;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.account.Gender;
import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.account.Name;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public class AccountFixture {
    private UniqueIdentifier uid;
    private LocationId defaultShippingLocationId;
    private Name name;
    private Birthdate birthDate;
    private Gender gender;
    private IDUL idul;
    private Email email;
    private List<Subscription> subscriptions = new ArrayList<>();

    public AccountFixture withAccountId(UniqueIdentifier uid) {
        this.uid = uid;
        return this;
    }

    public AccountFixture withDefaultShippingLocationId(LocationId locationId) {
        this.defaultShippingLocationId = locationId;
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

    public Account build() {
        Account account = new Account(uid, name, birthDate, gender, idul, email);
        return account;
    }
}
