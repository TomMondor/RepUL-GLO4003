package ca.ulaval.glo4003.repul.config.seed;

import ca.ulaval.glo4003.repul.config.seed.cooking.CookingSeed;
import ca.ulaval.glo4003.repul.config.seed.delivery.DeliverySeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.IdentityManagementSeed;
import ca.ulaval.glo4003.repul.config.seed.lockerauthorization.LockerAuthorizationSeed;
import ca.ulaval.glo4003.repul.config.seed.notification.NotificationSeed;
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

public interface SeedFactory {
    IdentityManagementSeed createIdentityManagementSeed(UserFactory userFactory, UserRepository userRepository);

    CookingSeed createCookingSeed(KitchenPersister kitchenPersister);

    DeliverySeed createDeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog);

    LockerAuthorizationSeed createLockerAuthorizationSeed(LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister);

    NotificationSeed createNotificationSeed(UserAccountRepository userAccountRepository, DeliveryPersonAccountRepository deliveryPersonAccountRepository);

    SubscriptionSeed createSubscriptionSeed(SubscriberRepository subscriberRepository);
}
