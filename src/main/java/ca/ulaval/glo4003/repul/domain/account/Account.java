package ca.ulaval.glo4003.repul.domain.account;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.Email;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public class Account {
    private final LocationId defaultShippingLocationId;
    private final Name name;
    private final Birthdate birthDate;
    private final Gender gender;
    private final IDUL idul;
    private final Email email;
    private List<Subscription> subscriptions = new ArrayList<>();

    public Account(LocationId defaultShippingLocationId, Name name, Birthdate birthDate, Gender gender, IDUL idul, Email email) {
        this.defaultShippingLocationId = defaultShippingLocationId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.idul = idul;
        this.email = email;
    }
}
