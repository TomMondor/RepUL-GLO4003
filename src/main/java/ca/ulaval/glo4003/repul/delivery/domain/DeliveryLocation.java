package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class DeliveryLocation {
    private final DeliveryLocationId deliveryLocationId;
    private final String name;
    private final int totalCapacity;
    private final List<Locker> lockers = new ArrayList<>();
    private final List<MealKit> waitingMealKit = new ArrayList<>();

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

    public Optional<Locker> findNextAvailableLocker() {
        return lockers.stream().filter(Locker::isUnassigned).sorted(
            Comparator.comparingInt(c -> c.getLockerId().lockerNumber())
        ).findFirst();
    }

    public Locker findLockerById(LockerId lockerId) {
        return lockers.stream().filter(locker -> locker.getLockerId().equals(lockerId)).findFirst().get();
    }
}
