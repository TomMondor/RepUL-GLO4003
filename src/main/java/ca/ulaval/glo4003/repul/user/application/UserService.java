package ca.ulaval.glo4003.repul.user.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.IDUL;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.event.UserCardAddedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.application.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.user.application.query.AddCardQuery;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.AccountFactory;
import ca.ulaval.glo4003.repul.user.domain.account.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory;
    private final TokenGenerator tokenGenerator;
    private final RepULEventBus eventBus;
    private final PasswordEncoder passwordEncoder;

    public UserService(AccountRepository accountRepository, UserRepository userRepository, AccountFactory accountFactory, UserFactory userFactory,
                       UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory, TokenGenerator tokenGenerator,
                       PasswordEncoder passwordEncoder, RepULEventBus eventBus) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountFactory = accountFactory;
        this.userFactory = userFactory;
        this.subscriberUniqueIdentifierFactory = subscriberUniqueIdentifierFactory;
        this.tokenGenerator = tokenGenerator;
        this.eventBus = eventBus;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationQuery registrationQuery) {
        if (userRepository.exists(registrationQuery.email())) {
            throw new EmailAlreadyInUseException();
        }
        if (accountRepository.exists(registrationQuery.idul())) {
            throw new IDULAlreadyInUseException();
        }
        UniqueIdentifier uniqueIdentifier = subscriberUniqueIdentifierFactory.generate();
        User newUser = userFactory.createUser(uniqueIdentifier, registrationQuery.email(), Role.CLIENT, registrationQuery.password());
        userRepository.save(newUser);

        Account createdAccount = accountFactory.createAccount(uniqueIdentifier, registrationQuery.idul(), registrationQuery.name(),
            registrationQuery.birthdate(), registrationQuery.gender(), registrationQuery.email());
        accountRepository.save(createdAccount);

        sendUserCreatedEvent(registrationQuery, createdAccount);
    }

    public Token login(LoginQuery loginQuery) {
        try {
            User user = userRepository.findByEmail(loginQuery.email());

            user.checkPasswordMatches(passwordEncoder, loginQuery.password());

            Token token = tokenGenerator.generate(user.getUid(), user.getRole());

            return token;
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }

    public AccountInformationPayload getAccount(UniqueIdentifier accountId) {
        Account account = accountRepository.getByAccountId(accountId);

        return AccountInformationPayload.fromAccount(account);
    }

    public void addCard(SubscriberUniqueIdentifier subscriberId, AddCardQuery addCardQuery) {
        Account account = accountRepository.getByAccountId(subscriberId);

        validateCardNumber(addCardQuery.cardNumber());
        account.setCardNumber(addCardQuery.cardNumber());
        accountRepository.save(account);

        eventBus.publish(new UserCardAddedEvent(subscriberId, addCardQuery.cardNumber()));
    }

    private void validateCardNumber(UserCardNumber cardNumber) {
        if (accountRepository.isUserCardNumberUsed(cardNumber)) {
            throw new CardNumberAlreadyInUseException();
        }
    }

    private void sendUserCreatedEvent(RegistrationQuery registrationQuery, Account createdAccount) {
        IDUL idul = new IDUL(registrationQuery.idul().value());
        Name name = new Name(registrationQuery.name().value());
        Birthdate birthdate = new Birthdate(registrationQuery.birthdate().value());
        Gender gender = Gender.from(registrationQuery.gender().name());

        UserCreatedEvent event = new UserCreatedEvent(createdAccount.getAccountId(), idul, name, birthdate, gender, createdAccount.getEmail());
        eventBus.publish(event);
    }
}
