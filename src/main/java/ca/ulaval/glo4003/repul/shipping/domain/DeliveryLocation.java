package ca.ulaval.glo4003.repul.shipping.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.CaseId;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;

public class DeliveryLocation {
    private final DeliveryLocationId deliveryLocationId;
    private final String name;
    private final int totalCapacity;
    private final List<Case> cases = new ArrayList<>();
    private final List<MealKitShippingInfo> waitingMealKit = new ArrayList<>();

    public DeliveryLocation(DeliveryLocationId deliveryLocationId, String name, int totalCapacity) {
        this.deliveryLocationId = deliveryLocationId;
        this.name = name;
        this.totalCapacity = totalCapacity;
        generateCases();
    }

    public DeliveryLocationId getLocationId() {
        return deliveryLocationId;
    }

    public String getName() {
        return name;
    }

    public int getRemainingCapacity() {
        return (int) cases.stream().filter(Case::isUnassigned).count();
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    private void generateCases() {
        for (int i = 0; i < totalCapacity; i++) {
            cases.add(new Case(this.generateCaseId(i + 1)));
        }
    }

    private CaseId generateCaseId(int caseNumber) {
        return new CaseId(this.name + " " + String.valueOf(caseNumber), caseNumber);
    }

    public CaseId assignCase(MealKitShippingInfo mealKit) {
        Optional<Case> assignedCase =
            cases.stream().filter(Case::isUnassigned).sorted(
                (c1, c2) -> Integer.compare(c1.getCaseId().caseNumber(), c2.getCaseId().caseNumber())
            ).findFirst();
        if (assignedCase.isEmpty()) {
            waitingMealKit.add(mealKit);
            return null;
        }
        assignedCase.get().assignCase(mealKit.getMealKitId());
        return assignedCase.get().getCaseId();
    }

    public void unassignCase(CaseId caseId) {
        cases.stream().filter(c -> c.getCaseId().equals(caseId)).findFirst().get().unassignCase();
        if (!waitingMealKit.isEmpty()) {
            waitingMealKit.remove(0).assignCase();
        }
    }
}
