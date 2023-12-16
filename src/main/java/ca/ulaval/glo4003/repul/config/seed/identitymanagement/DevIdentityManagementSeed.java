package ca.ulaval.glo4003.repul.config.seed.identitymanagement;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.env.EnvParserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Password;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Role;
import ca.ulaval.glo4003.repul.identitymanagement.domain.User;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;

public class DevIdentityManagementSeed extends IdentityManagementSeed {
    private static final EnvParser ENV_PARSER = EnvParserFactory.getEnvParser(".env");
    private static final CookUniqueIdentifier COOK_ID =
        new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom("f36ec513-6522-404e-a2b9-a4202b12c571");
    private static final RegistrationRequest COOK_REGISTRATION_REQUEST =
        new RegistrationRequest("PAUL123", "paul@ulaval.ca", "paul123", "Paul", "1990-01-01", "MAN");
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    private static final String DELIVERY_PERSON_EMAIL =
        ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL").isBlank() ? "roger@ulaval.ca" : ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL");
    private static final RegistrationRequest DELIVERY_PERSON_REGISTRATION_EMAIL =
        new RegistrationRequest("ROGER456", DELIVERY_PERSON_EMAIL, "roger123", "Roger", "1973-04-24", "MAN");

    public DevIdentityManagementSeed(UserFactory userFactory, UserRepository userRepository) {
        super(userFactory, userRepository);
    }

    @Override
    public void populate() {
        createCooks();
        createDeliveryPersons();
    }

    private void createCooks() {
        createAndSaveUser(COOK_ID, COOK_REGISTRATION_REQUEST, Role.COOK);
    }

    private void createDeliveryPersons() {
        createAndSaveUser(DELIVERY_PERSON_ID, DELIVERY_PERSON_REGISTRATION_EMAIL, Role.DELIVERY_PERSON);
    }

    private void createAndSaveUser(UniqueIdentifier clientId, RegistrationRequest clientRegistrationRequest, Role role) {
        User user = userFactory.createUser(clientId, new IDUL(clientRegistrationRequest.idul), new Email(clientRegistrationRequest.email), role,
            new Password(clientRegistrationRequest.password));

        userRepository.save(user);
    }
}
