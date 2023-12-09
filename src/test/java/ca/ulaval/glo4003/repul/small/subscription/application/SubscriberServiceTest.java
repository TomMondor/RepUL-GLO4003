package ca.ulaval.glo4003.repul.small.subscription.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriberFixture;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SubscriberServiceTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();

    private SubscriberService subscriberService;
    @Mock
    private SubscriberRepository subscriberRepository;
    @Mock
    private SubscriberFactory subscriberFactory;

    @BeforeEach
    public void createSubscriberService() {
        subscriberService = new SubscriberService(subscriberRepository, subscriberFactory);
    }

    @Test
    public void whenGettingSubscriberProfile_shouldReturnSubscriberProfilePayload() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        ProfilePayload expectedProfilePayload = ProfilePayload.from(subscriber.getProfile());

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);

        assertEquals(expectedProfilePayload, retrievedProfile);
    }
}
