package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

public class Subscriber {
    private final SubscriberUniqueIdentifier subscriberId;
    private final IDUL idul;
    private final Name name;
    private final Birthdate birthdate;
    private final Gender gender;
    private final Email email;
    private Optional<UserCardNumber> cardNumber = Optional.empty();
    private Map<SubscriptionUniqueIdentifier, Subscription> subscriptions = new HashMap<>();

    public Subscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        this.subscriberId = subscriberId;
        this.idul = idul;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
    }

    public SubscriberUniqueIdentifier getSubscriberId() {
        return subscriberId;
    }

    public IDUL getIdul() {
        return idul;
    }

    public Name getName() {
        return name;
    }

    public Birthdate getBirthdate() {
        return birthdate;
    }

    public Gender getGender() {
        return gender;
    }

    public Email getEmail() {
        return email;
    }

    public Optional<UserCardNumber> getCardNumber() {
        return cardNumber;
    }
}
