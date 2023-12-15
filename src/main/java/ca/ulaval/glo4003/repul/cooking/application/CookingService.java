package ca.ulaval.glo4003.repul.cooking.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitCookedDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.SelectionPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;

public class CookingService {
    private final KitchenPersister kitchenPersister;

    private final RepULEventBus eventBus;

    public CookingService(KitchenPersister kitchenPersister, RepULEventBus eventBus) {
        this.kitchenPersister = kitchenPersister;
        this.eventBus = eventBus;
    }

    public void createMealKitInPreparation(
        MealKitUniqueIdentifier mealKitId,
        SubscriptionUniqueIdentifier subscriptionId,
        SubscriberUniqueIdentifier subscriberId,
        MealKitType mealKitType,
        LocalDate deliveryDate,
        Optional<DeliveryLocationId> deliveryLocationId
    ) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.createMealKitInPreparation(mealKitId, subscriptionId, subscriberId, mealKitType, deliveryDate, deliveryLocationId);

        kitchenPersister.save(kitchen);
    }

    public void giveMealKitsToDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.pickUpMealKitsForDelivery(mealKitIds);

        kitchenPersister.save(kitchen);
    }

    public MealKitsPayload getMealKitsToCook() {
        Kitchen kitchen = kitchenPersister.get();
        List<MealKit> mealKitsToCook = kitchen.getMealKitsToCook();

        return MealKitsPayload.from(mealKitsToCook);
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.select(cookId, selectedMealKitIds);

        kitchenPersister.save(kitchen);
    }

    public SelectionPayload getSelection(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenPersister.get();

        return SelectionPayload.from(kitchen.getSelection(cookId));
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.cancelOneSelected(cookId, mealKitId);

        kitchenPersister.save(kitchen);
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.cancelAllSelected(cookId);

        kitchenPersister.save(kitchen);
    }

    public void confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        MealKit mealKit = kitchen.confirmCooked(cookId, mealKitId);

        kitchenPersister.save(kitchen);

        sendMealKitsCookedEvent(List.of(mealKit), kitchen);
    }

    public void confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        List<MealKit> mealKits = kitchen.confirmCooked(cookId, mealKitIds);

        kitchenPersister.save(kitchen);

        sendMealKitsCookedEvent(mealKits, kitchen);
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.recallCooked(cookId, mealKitId);

        kitchenPersister.save(kitchen);

        eventBus.publish(new RecallCookedMealKitEvent(mealKitId));
    }

    public void pickUpNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.pickUpNonDeliverableMealKit(subscriberId, mealKitId);

        kitchenPersister.save(kitchen);
    }

    private void sendMealKitsCookedEvent(List<MealKit> mealKits, Kitchen kitchen) {
        List<MealKitCookedDto> mealKitCookedDtos = mealKits.stream().map(
            mealKit -> new MealKitCookedDto(mealKit.toDto(), mealKit.isToBeDelivered())
        ).toList();
        eventBus.publish(new MealKitsCookedEvent(kitchen.getKitchenLocationId().toString(), mealKitCookedDtos));
    }
}
