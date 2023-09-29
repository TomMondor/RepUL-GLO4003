package ca.ulaval.glo4003.repul.domain.account.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

public class SubscriptionFactory {
    private final UniqueIdentifierFactory uniqueIdentifierFactory;

    public SubscriptionFactory(UniqueIdentifierFactory uniqueIdentifierFactory) {
        this.uniqueIdentifierFactory = uniqueIdentifierFactory;
    }

    public Subscription createSubscription(LocalDate startDate, LocalDate endDate, PickupLocation pickupLocation, Lunchbox lunchbox, DayOfWeek dayOfWeek,
                                           LunchboxType lunchboxType) {
        LocalDate firstOrderDate = getFirstOrderDate(startDate, endDate, dayOfWeek);
        List<Order> orders = createOrdersInSemester(firstOrderDate, endDate, dayOfWeek, lunchbox);

        return new Subscription(uniqueIdentifierFactory.generate(), orders, new Frequency(dayOfWeek), pickupLocation, startDate, lunchboxType);
    }

    private LocalDate getFirstOrderDate(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek) {
        LocalDate firstOrderDate = startDate;

        while (firstOrderDate.isBefore(endDate) && firstOrderDate.getDayOfWeek() != dayOfWeek) {
            firstOrderDate = firstOrderDate.plusDays(1);
        }

        return firstOrderDate;
    }

    private List<Order> createOrdersInSemester(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek, Lunchbox lunchbox) {
        LocalDate firstOrderDate = startDate;
        List<Order> orders = new ArrayList<>();

        while (firstOrderDate.isBefore(endDate)) {
            if (firstOrderDate.getDayOfWeek() == dayOfWeek) {
                orders.add(new Order(uniqueIdentifierFactory.generate(), lunchbox, firstOrderDate));
            }
            firstOrderDate = firstOrderDate.plusDays(7);
        }

        return orders;
    }
}
