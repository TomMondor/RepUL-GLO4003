package ca.ulaval.glo4003.repul.user.application;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCardAddedEvent;
import ca.ulaval.glo4003.repul.user.application.exception.AccountNotFoundException;
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
    private final UniqueIdentifierFactory uniqueIdentifierFactory;
    private final TokenGenerator tokenGenerator;
    private final RepULEventBus eventBus;
    private final PasswordEncoder passwordEncoder;

    public UserService(AccountRepository accountRepository, UserRepository userRepository, AccountFactory accountFactory, UserFactory userFactory,
                       UniqueIdentifierFactory uniqueIdentifierFactory, TokenGenerator tokenGenerator, PasswordEncoder passwordEncoder,
                       RepULEventBus eventBus) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountFactory = accountFactory;
        this.userFactory = userFactory;
        this.uniqueIdentifierFactory = uniqueIdentifierFactory;
        this.tokenGenerator = tokenGenerator;
        this.eventBus = eventBus;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationQuery registrationQuery) {
        if (userRepository.exists(registrationQuery.email())) {
            throw new EmailAlreadyInUseException();
        }
        if (accountRepository.getByIDUL(registrationQuery.idul()).isPresent()) {
            throw new IDULAlreadyInUseException();
        }
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierFactory.generate();
        User newUser = userFactory.createUser(uniqueIdentifier, registrationQuery.email(), Role.CLIENT, registrationQuery.password());
        userRepository.save(newUser);

        Account createdAccount =
            accountFactory.createAccount(uniqueIdentifier, registrationQuery.idul(), registrationQuery.name(), registrationQuery.birthdate(),
                registrationQuery.gender(), registrationQuery.email());
        accountRepository.save(createdAccount);

        eventBus.publish(new AccountCreatedEvent(createdAccount.getAccountId(), createdAccount.getEmail()));
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
        Optional<Account> account = accountRepository.getByAccountId(accountId);
        if (account.isEmpty()) {
            throw new AccountNotFoundException();
        }

        return AccountInformationPayload.fromAccount(account.get());
    }

    public void addCard(UniqueIdentifier accountId, AddCardQuery addCardQuery) {
        Optional<Account> account = accountRepository.getByAccountId(accountId);
        if (account.isEmpty()) {
            throw new AccountNotFoundException();
        }
        validateCardNumber(addCardQuery.cardNumber());
        account.get().setCardNumber(addCardQuery.cardNumber());
        accountRepository.save(account.get());

        eventBus.publish(new UserCardAddedEvent(accountId, addCardQuery.cardNumber()));
    }

    private void validateCardNumber(UserCardNumber cardNumber) {
        if (accountRepository.isUserCardNumberUsed(cardNumber)) {
            throw new CardNumberAlreadyInUseException();
        }
    }
}
