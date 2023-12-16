package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Profile;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.ProcessConfirmationDto;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscriptions;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrderFactory;

public class Subscriber {
    private final SubscriberUniqueIdentifier subscriberId;
    private final Profile profile;
    private final Subscriptions subscriptions = new Subscriptions();

    public Subscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        this.subscriberId = subscriberId;
        this.profile = new Profile(idul, name, birthdate, gender, email);
    }

    public SubscriberUniqueIdentifier getSubscriberId() {
        return subscriberId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptions.getAll();
    }

    public Subscription getSubscription(SubscriptionUniqueIdentifier subscriptionId) {
        return subscriptions.get(subscriptionId);
    }

    public Optional<SubscriberCardNumber> getCardNumber() {
        return profile.getCardNumber();
    }

    public void setCardNumber(SubscriberCardNumber cardNumber) {
        profile.setCardNumber(cardNumber);
    }

    public List<Order> getCurrentOrders() {
        return subscriptions.getCurrentOrders();
    }

    public Optional<ProcessConfirmationDto> confirm(SubscriptionUniqueIdentifier subscriptionUniqueIdentifier, OrderFactory orderFactory,
                                                    PaymentService paymentService) {
        return subscriptions.confirm(subscriptionUniqueIdentifier, subscriberId, orderFactory, paymentService);
    }

    public void decline(SubscriptionUniqueIdentifier subscriptionId) {
        subscriptions.decline(subscriptionId);
    }

    public List<ProcessConfirmationDto> processOrders(PaymentService paymentService) {
        return subscriptions.processOrders(subscriberId, paymentService);
    }

    public boolean hasOrder(MealKitUniqueIdentifier orderId) {
        return subscriptions.hasOrder(orderId);
    }

    public void updateOrderToInDelivery(MealKitUniqueIdentifier orderId) {
        subscriptions.updateToInDelivery(orderId);
    }

    public void updateOrderToInPreparation(MealKitUniqueIdentifier orderId) {
        subscriptions.updateToInPreparation(orderId);
    }

    public void updateOrderToReadyToPickUp(MealKitUniqueIdentifier orderId) {
        subscriptions.updateToReadyForPickUp(orderId);
    }

    public void updateOrderToPickedUp(MealKitUniqueIdentifier orderId) {
        subscriptions.updateToPickedUp(orderId);
    }
}
