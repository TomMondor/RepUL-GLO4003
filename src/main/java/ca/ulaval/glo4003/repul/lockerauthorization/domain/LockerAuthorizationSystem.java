package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.SubscriberCardNotAuthorizedException;

public class LockerAuthorizationSystem {
    private final Map<SubscriberUniqueIdentifier, SubscriberCardNumber>
        subscriberCardNumberBySubscriber = new HashMap<>();
    private final Map<MealKitUniqueIdentifier, Order> orders = new HashMap<>();

    public void registerSubscriberCardNumber(SubscriberUniqueIdentifier subscriberId, SubscriberCardNumber subscriberCardNumber) {
        subscriberCardNumberBySubscriber.put(subscriberId, subscriberCardNumber);
    }

    public void createOrder(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId) {
        orders.put(mealKitId, new Order(subscriberId, subscriptionId, mealKitId));
    }

    public void assignLocker(MealKitUniqueIdentifier mealKitId, LockerId lockerId) {
        Order order = getOrderFrom(mealKitId);
        order.assignLocker(lockerId);
    }

    public void unassignLocker(MealKitUniqueIdentifier mealKitId) {
        Order order = getOrderFrom(mealKitId);
        order.unassignLocker();
    }

    public MealKitDto authorize(LockerId lockerId, SubscriberCardNumber subscriberCardNumberToValidate) {
        Order order = getOrderByLockerId(lockerId);
        if (subscriberCardNumberBySubscriber.containsKey(order.getSubscriberId())) {
            SubscriberCardNumber subscriberCardNumber = subscriberCardNumberBySubscriber.get(order.getSubscriberId());
            if (subscriberCardNumber.equals(subscriberCardNumberToValidate)) {
                removeTakenOrder(order);
            } else {
                throw new SubscriberCardNotAuthorizedException();
            }
        } else {
            throw new NoCardLinkedToUserException();
        }
        return new MealKitDto(order.getSubscriberId(), order.getSubscriptionId(), order.getMealKitId());
    }

    private Order getOrderByLockerId(LockerId lockerId) {
        return orders.values().stream().filter(order -> order.hasLockerId(lockerId))
            .findFirst().orElseThrow(LockerNotAssignedException::new);
    }

    private void removeTakenOrder(Order order) {
        orders.remove(order.getMealKitId());
    }

    private Order getOrderFrom(MealKitUniqueIdentifier mealKitId) {
        Order order = orders.get(mealKitId);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        return order;
    }
}
