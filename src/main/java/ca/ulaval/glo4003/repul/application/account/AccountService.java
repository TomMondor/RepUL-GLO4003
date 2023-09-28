package ca.ulaval.glo4003.repul.application.account;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.application.AuthFacade;
import ca.ulaval.glo4003.repul.application.account.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.application.account.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.AccountFactory;

public class AccountService {
    private final RepULRepository repULRepository;
    private final AccountFactory accountFactory;

    private final AuthFacade authFacade;

    public AccountService(RepULRepository repULRepository, AccountFactory accountFactory, AuthFacade authFacade) {
        this.repULRepository = repULRepository;
        this.accountFactory = accountFactory;
        this.authFacade = authFacade;
    }

    public void register(RegistrationQuery registrationQuery) {
        RepUL repUL = repULRepository.get();

        UniqueIdentifier accountId = authFacade.register(registrationQuery.email().value(), registrationQuery.password());

        Account createdAccount = accountFactory.createAccount(accountId, registrationQuery.name(), registrationQuery.birthdate(), registrationQuery.gender(),
            registrationQuery.idul(), registrationQuery.email());
        repUL.addAccount(createdAccount);

        repULRepository.saveOrUpdate(repUL);
    }

    public AccountInformationPayload getAccount(UniqueIdentifier accountId) {
        RepUL repUL = repULRepository.get();

        Account account = repUL.findAccountById(accountId);

        return AccountInformationPayload.fromAccount(account);
    }
}
