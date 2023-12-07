package ca.ulaval.glo4003.repul.small.subscription.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriberNotFound;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemorySubscriberRepositoryTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_VALID_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();

    private InMemorySubscriberRepository inMemorySubscriberRepository;
    @Mock
    private Subscriber subscriber;

    @BeforeEach
    public void createSubscriberRepository() {
        inMemorySubscriberRepository = new InMemorySubscriberRepository();
    }

    @Test
    public void whenSavingSubscriberAndGettingSubscriberById_shouldReturnTheRightSubscriber() {
        given(subscriber.getSubscriberId()).willReturn(A_SUBSCRIBER_VALID_ID);

        inMemorySubscriberRepository.save(subscriber);
        Subscriber subscriberFound = inMemorySubscriberRepository.getById(A_SUBSCRIBER_VALID_ID);

        assertEquals(subscriber, subscriberFound);
    }

    @Test
    public void givenNoSubscriber_whenGettingSubscriberById_shouldThrowSubscriberNotFoundException() {
        assertThrows(SubscriberNotFound.class, () -> inMemorySubscriberRepository.getById(A_SUBSCRIBER_VALID_ID));
    }
}
