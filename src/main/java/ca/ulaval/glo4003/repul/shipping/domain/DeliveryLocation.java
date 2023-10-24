package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;

public class DeliveryLocation {
    private final DeliveryLocationId deliveryLocationId;
    private final String name;
    private final int totalCapacity;
    private final List<Locker> lockers = new ArrayList<>();
    private final List<MealKitShippingInfo> waitingMealKit = new ArrayList<>();

    public DeliveryLocation(DeliveryLocationId deliveryLocationId, String name, int totalCapacity) {
        this.deliveryLocationId = deliveryLocationId;
        this.name = name;
        this.totalCapacity = totalCapacity;
        generateLockers();
    }

    public DeliveryLocationId getLocationId() {
        return deliveryLocationId;
    }

    public String getName() {
        return name;
    }

    public int getRemainingCapacity() {
        return (int) lockers.stream().filter(Locker::isUnassigned).count();
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    private void generateLockers() {
        for (int i = 0; i < totalCapacity; i++) {
            lockers.add(new Locker(this.generateLockerId(i + 1)));
        }
    }

    private LockerId generateLockerId(int lockerNumber) {
        return new LockerId(this.name + " " + String.valueOf(lockerNumber), lockerNumber);
    }

    public LockerId assignLocker(MealKitShippingInfo mealKit) {
        Optional<Locker> assignedLocker =
            lockers.stream().filter(Locker::isUnassigned).sorted(
                Comparator.comparingInt(c -> c.getLockerId().lockerNumber())
            ).findFirst();
        if (assignedLocker.isEmpty()) {
            waitingMealKit.add(mealKit);
            return null;
        }
        assignedLocker.get().assign(mealKit.getMealKitId());
        return assignedLocker.get().getLockerId();
    }

    public void unassignLocker(LockerId lockerId) {
        lockers.stream().filter(c -> c.getLockerId().equals(lockerId)).findFirst().get().unassign();
        if (!waitingMealKit.isEmpty()) {
            waitingMealKit.remove(0).assignLocker();
        }
    }
}
