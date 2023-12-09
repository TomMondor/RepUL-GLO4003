package ca.ulaval.glo4003.repul.subscription.application;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.IDUL;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SubscriberFactory subscriberFactory;

    public SubscriberService(SubscriberRepository subscriberRepository, SubscriberFactory subscriberFactory) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberFactory = subscriberFactory;
    }

    public void createSubscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        Subscriber subscriber = subscriberFactory.createSubscriber(subscriberId, idul, name, birthdate, gender, email);

        subscriberRepository.save(subscriber);
    }

    public ProfilePayload getSubscriberProfile(SubscriberUniqueIdentifier subscriberId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        return ProfilePayload.from(subscriber.getProfile());
    }
}
