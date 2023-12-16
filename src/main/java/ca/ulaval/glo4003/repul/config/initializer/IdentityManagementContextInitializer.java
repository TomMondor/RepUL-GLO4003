package ca.ulaval.glo4003.repul.config.initializer;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.identitymanagement.api.UserResource;
import ca.ulaval.glo4003.repul.identitymanagement.application.UserService;
import ca.ulaval.glo4003.repul.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.TokenDecoder;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.TokenGenerator;
import ca.ulaval.glo4003.repul.identitymanagement.infrastructure.CryptPasswordEncoder;
import ca.ulaval.glo4003.repul.identitymanagement.infrastructure.InMemoryUserRepository;
import ca.ulaval.glo4003.repul.identitymanagement.infrastructure.JWTTokenDecoder;
import ca.ulaval.glo4003.repul.identitymanagement.infrastructure.JWTTokenGenerator;
import ca.ulaval.glo4003.repul.identitymanagement.middleware.AuthGuard;

import com.auth0.jwt.algorithms.Algorithm;

public class IdentityManagementContextInitializer implements ContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityManagementContextInitializer.class);
    private final UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class);
    private final RepULEventBus eventBus;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;
    private final EnvParser envParser = EnvParser.getInstance();
    private final Algorithm encryptionAlgorithm = Algorithm.HMAC256(envParser.readVariable("JWT_SECRET"));
    private final TokenDecoder tokenDecoder = new JWTTokenDecoder(encryptionAlgorithm);
    private final TokenGenerator tokenGenerator = new JWTTokenGenerator(encryptionAlgorithm);
    private final SeedFactory seedFactory;
    private final UserRepository userRepository = new InMemoryUserRepository();

    public IdentityManagementContextInitializer(RepULEventBus eventBus, SeedFactory seedFactory) {
        this.eventBus = eventBus;
        this.passwordEncoder = new CryptPasswordEncoder();
        this.userFactory = new UserFactory(passwordEncoder);
        this.seedFactory = seedFactory;
    }

    @Override
    public void initialize(ResourceConfig resourceConfig) {
        UserService userService = createUserService();
        AuthGuard authGuard = createAuthGuard();
        UserResource userResource = new UserResource(userService);
        populate();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(userResource).to(UserResource.class);
            }
        };

        resourceConfig.register(binder).register(authGuard);
    }

    private UserService createUserService() {
        LOGGER.info("Creating User service");
        UserService service = new UserService(userRepository, userFactory, subscriberUniqueIdentifierFactory, tokenGenerator, passwordEncoder, eventBus);
        eventBus.register(service);
        return service;
    }

    private AuthGuard createAuthGuard() {
        LOGGER.info("Creating Auth guard");
        return new AuthGuard(userRepository, tokenDecoder);
    }

    private void populate() {
        Seed seed =  seedFactory.createIdentityManagementSeed(userFactory, userRepository);
        seed.populate();
    }
}
