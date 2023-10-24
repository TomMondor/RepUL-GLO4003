package ca.ulaval.glo4003.repul.config.initializer;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.AccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.LogNotificationSender;

public class NotificationContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationContextInitializer.class);

    private AccountRepository accountRepository = new InMemoryAccountRepository();
    private NotificationSender notificationSender = new LogNotificationSender();

    public NotificationContextInitializer withAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        return this;
    }

    public NotificationContextInitializer withNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
        return this;
    }

    public NotificationContextInitializer withAccounts(List<Map<UniqueIdentifier, String>> accounts) {
        accounts.forEach(account -> account.forEach((id, email) -> accountRepository.saveOrUpdate(new Account(id, new Email(email)))));
        return this;
    }

    public NotificationService createNotificationService(RepULEventBus eventBus) {
        LOGGER.info("Creating Notification service");
        NotificationService notificationService = new NotificationService(accountRepository, notificationSender);
        eventBus.register(notificationService);
        return notificationService;
    }
}
