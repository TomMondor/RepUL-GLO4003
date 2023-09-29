package ca.ulaval.glo4003.config;

import java.time.LocalDate;

import org.glassfish.jersey.server.ResourceConfig;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.identitymanagement.infrastructure.CryptPasswordEncoder;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.AccountFactory;
import ca.ulaval.glo4003.repul.domain.account.Birthdate;
import ca.ulaval.glo4003.repul.domain.account.Gender;
import ca.ulaval.glo4003.repul.domain.account.IDUL;
import ca.ulaval.glo4003.repul.domain.account.Name;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;

public class DemoApplicationContext extends DevApplicationContext {
    @Override
    public ResourceConfig initializeResourceConfig() {
        ResourceConfig resourceConfig = super.initializeResourceConfig();
        RepUL repUL = repULRepository.get();

        // Create user for client
        User clientUser = createClientUser();
        userRepository.saveOrUpdate(clientUser);

        // Create account for client
        // email: client@ulaval.ca
        // password: client
        Account clientAccount = createClientAccount(clientUser.getUid());
        repUL.addAccount(clientAccount);

        // Create new subscription
        repUL.createSubscription(
            clientAccount.getAccountId(),
            new LocationId("VACHON"),
            LocalDate.now().getDayOfWeek().plus(1),
            LunchboxType.STANDARD);

        // Change first order of subscription to be delivered tomorrow
        repUL.getSubscriptions(clientAccount.getAccountId()).get(0).getOrders().get(0).setOrderStatus(OrderStatus.TO_COOK);

        // Save RepUL
        repULRepository.saveOrUpdate(repUL);

        return resourceConfig;
    }

    private Account createClientAccount(UniqueIdentifier accountId) {
        AccountFactory accountFactory = new AccountFactory();

        Name name = new Name("Client");
        Birthdate birthdate = new Birthdate(LocalDate.now().minusDays(2500));
        Gender gender = Gender.WOMAN;
        IDUL idul = new IDUL("CLIEN10");
        Email email = new Email("client@ulaval.ca");

        return accountFactory.createAccount(accountId, name, birthdate, gender, idul, email);
    }

    private User createClientUser() {
        UserFactory userFactory = new UserFactory(new CryptPasswordEncoder());

        Email email = new Email("client@ulaval.ca");
        Password password = new Password("client");
        Role role = Role.CLIENT;
        UniqueIdentifier uid = new UniqueIdentifierFactory().generate();

        return userFactory.createUser(uid, email, role, password);
    }
}
