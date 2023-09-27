package ca.ulaval.glo4003.repul.domain.account;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.exception.SubscriptionNotFoundException;

public class Account {
    private final UniqueIdentifier accountId;
    private final Name name;
    private final Birthdate birthDate;
    private final Gender gender;
    private final IDUL idul;
    private final Email email;
    private final List<Subscription> subscriptions = new ArrayList<>();

    public Account(UniqueIdentifier accountId, Name name, Birthdate birthDate, Gender gender, IDUL idul, Email email) {
        this.accountId = accountId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.idul = idul;
        this.email = email;
    }

    public UniqueIdentifier getAccountId() {
        return accountId;
    }

    public void confirmNextLunchbox(UniqueIdentifier subscriptionId) {
        Subscription subscription = findSubscriptionById(subscriptionId);
        subscription.confirmNextLunchbox();
    }

    public void declineNextLunchbox(UniqueIdentifier subscriptionId) {
        Subscription subscription = findSubscriptionById(subscriptionId);
        subscription.declineNextLunchbox();
    }

    private Subscription findSubscriptionById(UniqueIdentifier subscriptionId) {
        return subscriptions.stream().filter(subscription -> subscription.getSubscriptionId().equals(subscriptionId)).findFirst()
            .orElseThrow(SubscriptionNotFoundException::new);
    }
}
