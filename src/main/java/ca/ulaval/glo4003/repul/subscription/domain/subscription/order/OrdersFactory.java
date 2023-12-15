package ca.ulaval.glo4003.repul.subscription.domain.subscription.order;

import java.time.DayOfWeek;
import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoOrdersInDesiredPeriodException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;

public class OrdersFactory implements OrderFactory {
    UniqueIdentifierFactory<MealKitUniqueIdentifier> uniqueIdentifierFactory;

    public OrdersFactory(UniqueIdentifierFactory<MealKitUniqueIdentifier> uniqueIdentifierFactory) {
        this.uniqueIdentifierFactory = uniqueIdentifierFactory;
    }

    public Orders createOrdersInSemester(LocalDate today, LocalDate semesterEndDate, DayOfWeek desiredDayOfWeek, MealKitType mealKitType) {
        LocalDate nextOrderDate = getFirstOrderDate(today, semesterEndDate, desiredDayOfWeek);
        Orders orders = new Orders();

        while (nextOrderDate.isBefore(semesterEndDate.plusDays(1))) {
            Order order = new Order(uniqueIdentifierFactory.generate(), mealKitType, nextOrderDate);
            orders.add(order);
            nextOrderDate = nextOrderDate.plusDays(7);
        }

        return orders;
    }

    private LocalDate getFirstOrderDate(LocalDate today, LocalDate semesterEndDate, DayOfWeek dayOfWeek) {
        LocalDate firstOrderDateCandidate = today;
        while (firstOrderDateCandidate.getDayOfWeek() != dayOfWeek) {
            firstOrderDateCandidate = firstOrderDateCandidate.plusDays(1);

            if (firstOrderDateCandidate.isAfter(semesterEndDate)) {
                throw new NoOrdersInDesiredPeriodException();
            }
        }
        return firstOrderDateCandidate;
    }

    @Override
    public Order createSporadicOrder(Semester semester, MealKitType mealKitType) {
        LocalDate dateOfReceipt = LocalDate.now().plusDays(2);
        if (!semester.includes(dateOfReceipt)) {
            throw new OrderCannotBeConfirmedException();
        }

        Order createdOrder = new Order(uniqueIdentifierFactory.generate(), mealKitType, dateOfReceipt);

        return createdOrder;
    }
}
