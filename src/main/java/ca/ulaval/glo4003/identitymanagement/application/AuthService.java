package ca.ulaval.glo4003.identitymanagement.application;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.identitymanagement.application.query.RegistrationQuery;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenGenerator;

public class AuthService implements AuthFacade {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final UniqueIdentifierFactory uniqueIdentifierFactory;
    private final TokenGenerator tokenGenerator;

    public AuthService(UserRepository userRepository, UserFactory userFactory, UniqueIdentifierFactory uniqueIdentifierFactory, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.uniqueIdentifierFactory = uniqueIdentifierFactory;
        this.tokenGenerator = tokenGenerator;
    }

    public UniqueIdentifier register(String email, String password) {
        RegistrationQuery registrationQuery = RegistrationQuery.from(email, password);

        if (userRepository.exists(registrationQuery.email())) {
            throw new UserAlreadyExistsException();
        }

        User newUser = userFactory.createUser(uniqueIdentifierFactory.generate(), registrationQuery.email(), registrationQuery.password());
        userRepository.saveOrUpdate(newUser);

        return newUser.getUid();
    }
}
