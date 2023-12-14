package ca.ulaval.glo4003.repul.config.initializer;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenDecoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenGenerator;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.CryptPasswordEncoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.InMemoryUserRepository;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.JWTTokenDecoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.JWTTokenGenerator;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;

import com.auth0.jwt.algorithms.Algorithm;

public class UserContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextInitializer.class);
    private final UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class);
    private final RepULEventBus eventBus;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;
    private final EnvParser envParser = EnvParser.getInstance();
    private Algorithm encryptionAlgorithm = Algorithm.HMAC256(envParser.readVariable("JWT_SECRET"));
    private TokenDecoder tokenDecoder = new JWTTokenDecoder(encryptionAlgorithm);
    private TokenGenerator tokenGenerator = new JWTTokenGenerator(encryptionAlgorithm);
    private UserRepository userRepository = new InMemoryUserRepository();

    public UserContextInitializer(RepULEventBus eventBus) {
        this.eventBus = eventBus;
        this.passwordEncoder = new CryptPasswordEncoder();
        this.userFactory = new UserFactory(passwordEncoder);
    }

    public UserContextInitializer withUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public UserContextInitializer withTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
        return this;
    }

    public UserContextInitializer withTokenDecoder(TokenDecoder tokenDecoder) {
        this.tokenDecoder = tokenDecoder;
        return this;
    }

    public UserContextInitializer withClients(List<Map<UniqueIdentifier, RegistrationRequest>> requests) {
        requests.forEach(request -> createAndSaveUser(request, Role.CLIENT));
        return this;
    }

    public UserContextInitializer withCooks(List<Map<UniqueIdentifier, RegistrationRequest>> requests) {
        requests.forEach(request -> createAndSaveUser(request, Role.COOK));
        return this;
    }

    public UserContextInitializer withShippers(List<Map<UniqueIdentifier, RegistrationRequest>> requests) {
        requests.forEach(request -> createAndSaveUser(request, Role.DELIVERY_PERSON));
        return this;
    }

    public UserService createService() {
        LOGGER.info("Creating User service");
        UserService service = new UserService(userRepository, userFactory, subscriberUniqueIdentifierFactory, tokenGenerator, passwordEncoder, eventBus);
        eventBus.register(service);
        return service;
    }

    public AuthGuard createAuthGuard() {
        LOGGER.info("Creating Auth guard");
        return new AuthGuard(userRepository, tokenDecoder);
    }

    private void createAndSaveUser(Map<UniqueIdentifier, RegistrationRequest> query, Role role) {
        UniqueIdentifier userId = query.keySet().iterator().next();
        RegistrationRequest request = query.get(userId);
        User user = userFactory.createUser(userId, new IDUL(request.idul), new Email(request.email), role, new Password(request.password));
        userRepository.save(user);
    }
}
