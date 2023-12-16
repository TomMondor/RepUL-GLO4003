package ca.ulaval.glo4003.repul.config.seed;

import ca.ulaval.glo4003.repul.config.seed.cooking.CookingSeed;
import ca.ulaval.glo4003.repul.config.seed.cooking.TestCookingSeed;
import ca.ulaval.glo4003.repul.config.seed.delivery.DeliverySeed;
import ca.ulaval.glo4003.repul.config.seed.delivery.TestDeliverySeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.IdentityManagementSeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.TestIdentityManagementSeed;
import ca.ulaval.glo4003.repul.config.seed.lockerauthorization.LockerAuthorizationSeed;
import ca.ulaval.glo4003.repul.config.seed.lockerauthorization.TestLockerAuthorizationSeed;
import ca.ulaval.glo4003.repul.config.seed.notification.NotificationSeed;
import ca.ulaval.glo4003.repul.config.seed.notification.TestNotificationSeed;
import ca.ulaval.glo4003.repul.config.seed.subscription.SubscriptionSeed;
import ca.ulaval.glo4003.repul.config.seed.subscription.TestSubscriptionSeed;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;

public class TestSeedFactory implements SeedFactory {
    public IdentityManagementSeed createIdentityManagementSeed(UserFactory userFactory, UserRepository userRepository) {
        return new TestIdentityManagementSeed(userFactory, userRepository);
    }

    public CookingSeed createCookingSeed(KitchenPersister kitchenPersister) {
        return new TestCookingSeed(kitchenPersister);
    }

    public DeliverySeed createDeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog) {
        return new TestDeliverySeed(deliverySystemPersister, locationsCatalog);
    }

    public LockerAuthorizationSeed createLockerAuthorizationSeed(LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister) {
        return new TestLockerAuthorizationSeed(lockerAuthorizationSystemPersister);
    }

    public NotificationSeed createNotificationSeed(UserAccountRepository userAccountRepository,
                                                   DeliveryPersonAccountRepository deliveryPersonAccountRepository) {
        return new TestNotificationSeed(userAccountRepository, deliveryPersonAccountRepository);
    }

    public SubscriptionSeed createSubscriptionSeed(SubscriberRepository subscriberRepository) {
        return new TestSubscriptionSeed(subscriberRepository);
    }
}
