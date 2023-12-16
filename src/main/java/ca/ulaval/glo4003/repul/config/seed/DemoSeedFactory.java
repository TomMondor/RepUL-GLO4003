package ca.ulaval.glo4003.repul.config.seed;

import ca.ulaval.glo4003.repul.config.seed.cooking.CookingSeed;
import ca.ulaval.glo4003.repul.config.seed.cooking.DemoCookingSeed;
import ca.ulaval.glo4003.repul.config.seed.delivery.DeliverySeed;
import ca.ulaval.glo4003.repul.config.seed.delivery.DemoDeliverySeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.DemoIdentityManagementSeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.IdentityManagementSeed;
import ca.ulaval.glo4003.repul.config.seed.lockerauthorization.DemoLockerAuthorizationSeed;
import ca.ulaval.glo4003.repul.config.seed.lockerauthorization.LockerAuthorizationSeed;
import ca.ulaval.glo4003.repul.config.seed.notification.DemoNotificationSeed;
import ca.ulaval.glo4003.repul.config.seed.notification.NotificationSeed;
import ca.ulaval.glo4003.repul.config.seed.subscription.DemoSubscriptionSeed;
import ca.ulaval.glo4003.repul.config.seed.subscription.SubscriptionSeed;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;

public class DemoSeedFactory implements SeedFactory {
    public IdentityManagementSeed createIdentityManagementSeed(UserFactory userFactory, UserRepository userRepository) {
        return new DemoIdentityManagementSeed(userFactory, userRepository);
    }

    public CookingSeed createCookingSeed(KitchenPersister kitchenPersister) {
        return new DemoCookingSeed(kitchenPersister);
    }

    public DeliverySeed createDeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog) {
        return new DemoDeliverySeed(deliverySystemPersister, locationsCatalog);
    }

    public LockerAuthorizationSeed createLockerAuthorizationSeed(LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister) {
        return new DemoLockerAuthorizationSeed(lockerAuthorizationSystemPersister);
    }

    public NotificationSeed createNotificationSeed(UserAccountRepository userAccountRepository,
                                                   DeliveryPersonAccountRepository deliveryPersonAccountRepository) {
        return new DemoNotificationSeed(userAccountRepository, deliveryPersonAccountRepository);
    }

    public SubscriptionSeed createSubscriptionSeed(SubscriberRepository subscriberRepository) {
        return new DemoSubscriptionSeed(subscriberRepository);
    }
}
