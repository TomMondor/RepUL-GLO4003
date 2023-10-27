package ca.ulaval.glo4003.repul.notification.infrastructure;

import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;

public class LogNotificationSender implements NotificationSender {
    @Override
    public void send(Account account, String message) {
        System.out.println("Sending notification to: " + account.email().value() + " with message: " + message);
    }
}
