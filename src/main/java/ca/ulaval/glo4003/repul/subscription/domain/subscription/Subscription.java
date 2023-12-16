package ca.ulaval.glo4003.repul.subscription.domain.subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrderFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Orders;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.ConfirmedStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;

public class Subscription {
    private final SubscriptionUniqueIdentifier subscriptionId;
    private final Orders orders;
    private final Optional<WeeklyOccurence> weeklyOccurence;
    private final Optional<DeliveryLocationId> deliveryLocationId;
    private final LocalDate startDate;
    private final Semester semester;
    private final MealKitType mealKitType;

    public Subscription(SubscriptionUniqueIdentifier subscriptionId, Orders orders, Optional<WeeklyOccurence> weeklyOccurence,
                        Optional<DeliveryLocationId> deliveryLocationId, LocalDate startDate, Semester semester, MealKitType mealKitType) {
        this.subscriptionId = subscriptionId;
        this.orders = orders;
        this.weeklyOccurence = weeklyOccurence;
        this.deliveryLocationId = deliveryLocationId;
        this.startDate = startDate;
        this.semester = semester;
        this.mealKitType = mealKitType;
    }

    public SubscriptionUniqueIdentifier getSubscriptionId() {
        return subscriptionId;
    }

    public Optional<WeeklyOccurence> getWeeklyOccurence() {
        return weeklyOccurence;
    }

    public Optional<DeliveryLocationId> getDeliveryLocationId() {
        return deliveryLocationId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public MealKitType getMealKitType() {
        return mealKitType;
    }

    public Semester getSemester() {
        return semester;
    }

    public Optional<Order> getCurrentOrder() {
        return orders.getCurrent();
    }

    public List<Order> getCurrentOrders() {
        return orders.getCurrents();
    }

    public Optional<ProcessConfirmationDto> confirm(SubscriberUniqueIdentifier subscriberId, OrderFactory orderFactory, PaymentService paymentService) {
        if (isSporadic()) {
            return Optional.of(confirmSporadicOrder(subscriberId, orderFactory, paymentService));
        }

        confirmCurrentOrder();
        return Optional.empty();
    }

    public boolean isSporadic() {
        return weeklyOccurence.isEmpty();
    }

    private void confirmCurrentOrder() {
        orders.confirmCurrent();
    }

    private ProcessConfirmationDto confirmSporadicOrder(SubscriberUniqueIdentifier subscriberId, OrderFactory orderFactory, PaymentService paymentService) {
        Order order = orderFactory.createSporadicOrder(semester, mealKitType);

        order.changeStatus(new ConfirmedStatus(order));
        order.process(subscriberId, isSporadic(), paymentService);

        orders.add(order);

        return new ProcessConfirmationDto(subscriptionId, deliveryLocationId, List.of(order));
    }

    public void decline() {
        orders.declineCurrent();
    }

    public ProcessConfirmationDto processOrders(SubscriberUniqueIdentifier subscriberId, PaymentService paymentService) {
        List<Order> confirmedOrders = orders.process(subscriberId, isSporadic(), paymentService);

        return new ProcessConfirmationDto(subscriptionId, deliveryLocationId, confirmedOrders);
    }

    public boolean hasOrder(MealKitUniqueIdentifier orderId) {
        return orders.hasOrder(orderId);
    }

    public void updateToInDelivery(MealKitUniqueIdentifier orderId) {
        orders.updateToInDelivery(orderId);
    }

    public void updateToInPreparation(MealKitUniqueIdentifier orderId) {
        orders.updateToInPreparation(orderId);
    }

    public void updateToReadyForPickUp(MealKitUniqueIdentifier orderId) {
        orders.updateToReadyForPickUp(orderId);
    }

    public void updateToPickedUp(MealKitUniqueIdentifier orderId) {
        orders.updateToPickedUp(orderId);
    }
}
