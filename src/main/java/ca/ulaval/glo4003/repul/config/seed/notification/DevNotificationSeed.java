package ca.ulaval.glo4003.repul.config.seed.notification;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;

public class DevNotificationSeed extends NotificationSeed {
    public static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    public static final String DELIVERY_PERSON_EMAIL = "roger@ulaval.ca";

    public DevNotificationSeed(UserAccountRepository userAccountRepository, DeliveryPersonAccountRepository deliveryPersonAccountRepository) {
        super(userAccountRepository, deliveryPersonAccountRepository);
    }

    @Override
    public void populate() {
        createDeliveryPersonAccounts();
    }

    private void createDeliveryPersonAccounts() {
        deliveryPersonAccountRepository.save(new DeliveryPersonAccount(DELIVERY_PERSON_ID, new Email(DELIVERY_PERSON_EMAIL)));
    }
}
