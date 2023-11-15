package ca.ulaval.glo4003.repul.config.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemRepository;

public class LockerAuthorizationContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);

    private LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository = new InMemoryLockerAuthorizationSystemRepository();
    private List<Map.Entry<UniqueIdentifier, UniqueIdentifier>> orders = new ArrayList<>();

    public LockerAuthorizationContextInitializer withEmptyLockerAuthorizationSystemRepository(LockerAuthorizationSystemRepository repository) {
        lockerAuthorizationSystemRepository = repository;
        return this;
    }

    public LockerAuthorizationContextInitializer withOrders(List<Map.Entry<UniqueIdentifier, UniqueIdentifier>> orders) {
        this.orders.addAll(orders);
        return this;
    }

    public LockerAuthorizationService createLockerAuthorizationService(RepULEventBus eventBus) {
        LOGGER.info("Creating locker authorization service");
        initializeLockerAuthorization(lockerAuthorizationSystemRepository);
        LockerAuthorizationService service = new LockerAuthorizationService(eventBus, lockerAuthorizationSystemRepository);
        eventBus.register(service);
        return service;
    }

    private void initializeLockerAuthorization(LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository) {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        orders.forEach(order -> {
            UniqueIdentifier accountId = order.getKey();
            UniqueIdentifier mealKitId = order.getValue();
            lockerAuthorizationSystem.createOrder(accountId, mealKitId);
        });
        lockerAuthorizationSystemRepository.saveOrUpdate(lockerAuthorizationSystem);
    }
}
