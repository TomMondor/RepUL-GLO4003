package ca.ulaval.glo4003.repul.config.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationEventHandler;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;

public class LockerAuthorizationContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);

    private LockerAuthorizationSystemPersister
        lockerAuthorizationSystemPersister = new InMemoryLockerAuthorizationSystemPersister();
    private final List<Map.Entry<SubscriberUniqueIdentifier, MealKitDto>> orders = new ArrayList<>();

    public LockerAuthorizationContextInitializer withEmptyLockerAuthorizationSystemPersister(
        LockerAuthorizationSystemPersister repository) {
        lockerAuthorizationSystemPersister = repository;
        return this;
    }

    public LockerAuthorizationContextInitializer withOrders(List<Map.Entry<SubscriberUniqueIdentifier, MealKitDto>> orders) {
        this.orders.addAll(orders);
        return this;
    }

    public LockerAuthorizationService createLockerAuthorizationService(RepULEventBus eventBus) {
        LOGGER.info("Creating locker authorization service");
        initializeLockerAuthorization(lockerAuthorizationSystemPersister);
        return new LockerAuthorizationService(eventBus, lockerAuthorizationSystemPersister);
    }

    public void createLockerAuthorizationEventHandler(LockerAuthorizationService lockerAuthorizationService, RepULEventBus eventBus) {
        LockerAuthorizationEventHandler lockerAuthorizationEventHandler = new LockerAuthorizationEventHandler(lockerAuthorizationService);
        eventBus.register(lockerAuthorizationEventHandler);
    }

    private void initializeLockerAuthorization(
        LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister) {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        orders.forEach(order -> {
            SubscriberUniqueIdentifier accountId = order.getKey();
            SubscriptionUniqueIdentifier subscriptionId = order.getValue().subscriptionId();
            MealKitUniqueIdentifier mealKitId = order.getValue().mealKitId();
            lockerAuthorizationSystem.createOrder(accountId, subscriptionId, mealKitId);
        });
        lockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);
    }

    public ApiKeyGuard createApiKeyGuard() {
        return new ApiKeyGuard();
    }
}
