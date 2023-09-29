package ca.ulaval.glo4003.repul.domain.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.exception.SubscriptionNotFoundException;

public class Account {
    private final UniqueIdentifier accountId;
    private final Name name;
    private final Birthdate birthdate;
    private final Gender gender;
    private final IDUL idul;
    private final Email email;
    private final List<Subscription> subscriptions = new ArrayList<>();

    public Account(UniqueIdentifier accountId, Name name, Birthdate birthdate, Gender gender, IDUL idul, Email email) {
        this.accountId = accountId;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.idul = idul;
        this.email = email;
    }

    public Name getName() {
        return name;
    }

    public Birthdate getbirthdate() {
        return birthdate;
    }

    public Gender getGender() {
        return gender;
    }

    public IDUL getIdul() {
        return idul;
    }

    public Email getEmail() {
        return email;
    }

    public UniqueIdentifier getAccountId() {
        return accountId;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
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

    public List<Lunchbox> getLunchboxesToCook() {
        List<Lunchbox> lunchboxes = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            lunchboxes.addAll(subscription.getLunchboxesToCook());
        }
        return lunchboxes;
    }

    public List<Order> getCurrentOrders() {
        return subscriptions.stream().map(Subscription::findOptionalNextOrder).filter(Optional::isPresent).map(Optional::get).toList();
    }
}
