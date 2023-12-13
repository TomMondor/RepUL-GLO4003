package ca.ulaval.glo4003.repul.user.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenGenerator;

public class UserService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory;
    private final TokenGenerator tokenGenerator;
    private final RepULEventBus eventBus;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserFactory userFactory,
                       UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory, TokenGenerator tokenGenerator,
                       PasswordEncoder passwordEncoder, RepULEventBus eventBus) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.subscriberUniqueIdentifierFactory = subscriberUniqueIdentifierFactory;
        this.tokenGenerator = tokenGenerator;
        this.eventBus = eventBus;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Email email, Password password, IDUL idul, Name name, Birthdate birthdate, Gender gender) {
        if (userRepository.exists(email)) {
            throw new EmailAlreadyInUseException();
        }
        if (userRepository.exists(idul)) {
            throw new IDULAlreadyInUseException();
        }
        UniqueIdentifier uniqueIdentifier = subscriberUniqueIdentifierFactory.generate();
        User newUser = userFactory.createUser(uniqueIdentifier, idul, email, Role.CLIENT, password);
        userRepository.save(newUser);

        sendUserCreatedEvent(newUser, name, birthdate, gender, email);
    }

    public Token login(Email email, Password password) {
        try {
            User user = userRepository.findByEmail(email);

            user.checkPasswordMatches(passwordEncoder, password);

            Token token = tokenGenerator.generate(user.getUid(), user.getRole());

            return token;
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }

    private void sendUserCreatedEvent(User userCreated, Name name, Birthdate birthdate, Gender gender, Email email) {
        UserCreatedEvent event = new UserCreatedEvent(userCreated.getUid(), userCreated.getIdul(), name, birthdate, gender, email);
        eventBus.publish(event);
    }
}
