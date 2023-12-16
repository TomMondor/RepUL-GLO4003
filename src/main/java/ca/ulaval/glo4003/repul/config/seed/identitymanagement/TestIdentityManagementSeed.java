package ca.ulaval.glo4003.repul.config.seed.identitymanagement;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Password;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Role;
import ca.ulaval.glo4003.repul.identitymanagement.domain.User;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;

public class TestIdentityManagementSeed extends IdentityManagementSeed {
    public static final String CLIENT_EMAIL = "alexandra@ulaval.ca";
    public static final String CLIENT_PASSWORD = "alexandra123";
    public static final CookUniqueIdentifier COOK_ID =
        new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom("f36ec513-6522-404e-a2b9-a4202b12c571");
    public static final String COOK_EMAIL = "paul@ulaval.ca";
    public static final String COOK_PASSWORD = "paul123";
    public static final String DELIVERY_PERSON_EMAIL = "roger@ulaval.ca";
    public static final String DELIVERY_PERSON_PASSWORD = "roger123";
    public static final String SECOND_DELIVERY_PERSON_EMAIL = "john@ulaval.ca";
    public static final String SECOND_DELIVERY_PERSON_PASSWORD = "john123";
    private static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    private static final DeliveryPersonUniqueIdentifier SECOND_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("9db0ae10-501c-4efa-b922-41afe2dc75f1");
    private static final RegistrationRequest CLIENT_REGISTRATION_REQUEST =
        new RegistrationRequest("ALEXA123", CLIENT_EMAIL, CLIENT_PASSWORD, "Alexandra", "1999-01-01", "WOMAN");
    private static final RegistrationRequest COOK_REGISTRATION_REQUEST =
        new RegistrationRequest("PAUL123", COOK_EMAIL, COOK_PASSWORD, "Paul", "1990-01-01", "MAN");
    private static final RegistrationRequest DELIVERY_PERSON_REGISTRATION_REQUEST =
        new RegistrationRequest("ROGER456", DELIVERY_PERSON_EMAIL, DELIVERY_PERSON_PASSWORD, "Roger", "1973-04-24", "MAN");
    private static final RegistrationRequest SECOND_DELIVERY_PERSON_REGISTRATION_REQUEST =
        new RegistrationRequest("JOHN456", SECOND_DELIVERY_PERSON_EMAIL, SECOND_DELIVERY_PERSON_PASSWORD, "John", "1973-05-24", "MAN");

    public TestIdentityManagementSeed(UserFactory userFactory, UserRepository userRepository) {
        super(userFactory, userRepository);
    }

    @Override
    public void populate() {
        createClients();
        createCooks();
        createDeliveryPersons();
    }

    private void createClients() {
        createAndSaveUser(CLIENT_ID, CLIENT_REGISTRATION_REQUEST, Role.CLIENT);
    }

    private void createCooks() {
        createAndSaveUser(COOK_ID, COOK_REGISTRATION_REQUEST, Role.COOK);
    }

    private void createDeliveryPersons() {
        createAndSaveUser(DELIVERY_PERSON_ID, DELIVERY_PERSON_REGISTRATION_REQUEST, Role.DELIVERY_PERSON);
        createAndSaveUser(SECOND_DELIVERY_PERSON_ID, SECOND_DELIVERY_PERSON_REGISTRATION_REQUEST, Role.DELIVERY_PERSON);
    }

    private void createAndSaveUser(UniqueIdentifier clientId, RegistrationRequest clientRegistrationRequest, Role role) {
        User user = userFactory.createUser(clientId, new IDUL(clientRegistrationRequest.idul), new Email(clientRegistrationRequest.email), role,
            new Password(clientRegistrationRequest.password));

        userRepository.save(user);
    }
}
