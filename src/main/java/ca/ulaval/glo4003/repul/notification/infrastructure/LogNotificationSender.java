package ca.ulaval.glo4003.repul.notification.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;

public class LogNotificationSender implements NotificationSender {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LogNotificationSender.class);

    @Override
    public void send(Account account, String message) {
        LOGGER.info("Sending notification to: " + account.email().value() + " with message: " + message);
    }
}
