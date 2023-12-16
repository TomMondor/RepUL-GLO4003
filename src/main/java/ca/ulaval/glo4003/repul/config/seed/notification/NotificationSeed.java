package ca.ulaval.glo4003.repul.config.seed.notification;

import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;

public abstract class NotificationSeed implements Seed {
    protected final UserAccountRepository userAccountRepository;
    protected final DeliveryPersonAccountRepository deliveryPersonAccountRepository;

    protected NotificationSeed(UserAccountRepository userAccountRepository, DeliveryPersonAccountRepository deliveryPersonAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.deliveryPersonAccountRepository = deliveryPersonAccountRepository;
    }
}
