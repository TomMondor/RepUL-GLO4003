package ca.ulaval.glo4003.repul.subscription.application;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.SubscriberCardAddedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.ProcessConfirmation;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrderFactory;

public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SubscriberFactory subscriberFactory;
    private final SubscriptionFactory subscriptionFactory;
    private final RepULEventBus eventBus;
    private final PaymentService paymentService;
    private final OrderFactory orderFactory;

    public SubscriberService(SubscriberRepository subscriberRepository, SubscriberFactory subscriberFactory, SubscriptionFactory subscriptionFactory,
                             RepULEventBus eventBus, PaymentService paymentService, OrderFactory orderFactory) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberFactory = subscriberFactory;
        this.subscriptionFactory = subscriptionFactory;
        this.eventBus = eventBus;
        this.paymentService = paymentService;
        this.orderFactory = orderFactory;
    }

    public void createSubscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        Subscriber subscriber = subscriberFactory.createSubscriber(subscriberId, idul, name, birthdate, gender, email);

        subscriberRepository.save(subscriber);
    }

    public ProfilePayload getSubscriberProfile(SubscriberUniqueIdentifier subscriberId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        return ProfilePayload.from(subscriber.getProfile());
    }

    public SubscriptionUniqueIdentifier createSubscription(SubscriptionQuery subscriptionQuery) {
        Subscriber subscriber = subscriberRepository.getById(subscriptionQuery.subscriberId());

        Subscription newSubscription = subscriptionFactory.createSubscription(subscriptionQuery);
        subscriber.addSubscription(newSubscription);

        subscriberRepository.save(subscriber);

        return newSubscription.getSubscriptionId();
    }

    public SubscriptionsPayload getAllSubscriptions(SubscriberUniqueIdentifier subscriberId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        return SubscriptionsPayload.from(subscriber.getAllSubscriptions());
    }

    public SubscriptionPayload getSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        return SubscriptionPayload.from(subscriber.getSubscription(subscriptionId));
    }

    public OrdersPayload getCurrentOrders(SubscriberUniqueIdentifier subscriberId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        return OrdersPayload.from(subscriber.getCurrentOrders());
    }

    public void confirm(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        Optional<ProcessConfirmation> processedSporadic = subscriber.confirm(subscriptionId, orderFactory, paymentService);

        subscriberRepository.save(subscriber);

        if (processedSporadic.isPresent()) {
            sendMealKitConfirmedEvents(subscriberId, List.of(processedSporadic.get()));
        }
    }

    public void decline(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        subscriber.decline(subscriptionId);

        subscriberRepository.save(subscriber);
    }

    public void processOrders() {
        for (Subscriber subscriber : subscriberRepository.getAll()) {
            List<ProcessConfirmation> confirmedOrders = subscriber.processOrders(paymentService);

            subscriberRepository.save(subscriber);

            sendMealKitConfirmedEvents(subscriber.getSubscriberId(), confirmedOrders);
        }
    }

    private void sendMealKitConfirmedEvents(SubscriberUniqueIdentifier subscriberId, List<ProcessConfirmation> confirmedOrders) {
        for (ProcessConfirmation confirmation : confirmedOrders) {
            for (Order confirmedOrder : confirmation.confirmedOrders()) {
                MealKitConfirmedEvent event =
                    new MealKitConfirmedEvent(confirmedOrder.getOrderId(), confirmation.subscriptionId(), subscriberId, confirmedOrder.getMealKitType(),
                        confirmation.deliveryLocationId(), confirmedOrder.getDeliveryDate());

                eventBus.publish(event);
            }
        }
    }

    public void updateOrdersToInDelivery(List<MealKitUniqueIdentifier> orderIds) {
        for (MealKitUniqueIdentifier orderId : orderIds) {
            Subscriber subscriber = subscriberRepository.findByOrderId(orderId);

            subscriber.updateOrderToInDelivery(orderId);

            subscriberRepository.save(subscriber);
        }
    }

    public void updateOrdersToInPreparation(List<MealKitUniqueIdentifier> orderIds) {
        for (MealKitUniqueIdentifier orderId : orderIds) {
            Subscriber subscriber = subscriberRepository.findByOrderId(orderId);

            subscriber.updateOrderToInPreparation(orderId);

            subscriberRepository.save(subscriber);
        }
    }

    public void updateOrderToReadyToPickup(MealKitUniqueIdentifier orderId) {
        Subscriber subscriber = subscriberRepository.findByOrderId(orderId);

        subscriber.updateOrderToReadyToPickup(orderId);

        subscriberRepository.save(subscriber);
    }

    public void updateOrderToPickedUp(MealKitUniqueIdentifier orderId) {
        Subscriber subscriber = subscriberRepository.findByOrderId(orderId);

        subscriber.updateOrderToPickedUp(orderId);

        subscriberRepository.save(subscriber);
    }

    public void addCard(SubscriberUniqueIdentifier subscriberId, SubscriberCardNumber cardNumber) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        validateCardNumber(cardNumber);
        subscriber.setCardNumber(cardNumber);

        subscriberRepository.save(subscriber);
        eventBus.publish(new SubscriberCardAddedEvent(subscriberId, cardNumber));
    }

    private void validateCardNumber(SubscriberCardNumber cardNumber) {
        if (subscriberRepository.cardNumberExists(cardNumber)) {
            throw new CardNumberAlreadyInUseException();
        }
    }
}
