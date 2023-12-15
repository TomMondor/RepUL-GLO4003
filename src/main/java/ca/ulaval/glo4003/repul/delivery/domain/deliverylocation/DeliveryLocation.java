package ca.ulaval.glo4003.repul.delivery.domain.deliverylocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker.Locker;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class DeliveryLocation {
    private final DeliveryLocationId deliveryLocationId;
    private final String name;
    private final int totalCapacity;
    private final List<Locker> lockers;
    private final List<MealKit> waitingMealKits;

    public DeliveryLocation(DeliveryLocationId deliveryLocationId, String name, int totalCapacity) {
        this.deliveryLocationId = deliveryLocationId;
        this.name = name;
        this.totalCapacity = totalCapacity;
        waitingMealKits = new ArrayList<>();
        this.lockers = new ArrayList<>();
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
            LockerId lockerId = generateLockerId(i + 1);
            lockers.add(new Locker(lockerId));
        }
    }

    private LockerId generateLockerId(int lockerNumber) {
        return new LockerId(this.name + " " + lockerNumber, lockerNumber);
    }

    public void assignLocker(MealKit mealKit) {
        Optional<Locker> availableLocker = this.findNextAvailableLocker();

        if (availableLocker.isPresent()) {
            availableLocker.get().assign(mealKit);
            LockerId lockerId = availableLocker.get().getLockerId();
            mealKit.assignLocker(Optional.of(lockerId));
        } else {
            waitingMealKits.add(mealKit);
        }
    }

    private Optional<Locker> findNextAvailableLocker() {
        return lockers.stream().filter(Locker::isUnassigned).sorted(Comparator.comparingInt(c -> c.getLockerId().lockerNumber())).findFirst();
    }

    public void unassignLocker(MealKit mealKit) {
        this.findLockerById(mealKit.getLockerId().get()).unassign();
        if (!waitingMealKits.isEmpty()) {
            assignLocker(waitingMealKits.remove(0));
        }
    }

    public Locker findLockerById(LockerId lockerId) {
        return lockers.stream().filter(locker -> locker.getLockerId().equals(lockerId)).findFirst().orElseThrow(LockerNotFoundException::new);
    }
}
