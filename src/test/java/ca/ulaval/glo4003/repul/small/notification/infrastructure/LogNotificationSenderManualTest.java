package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.infrastructure.LogNotificationSender;

public class LogNotificationSenderManualTest {
    private static final Account AN_ACCOUNT = new Account(new UniqueIdentifierFactory().generate(), new Email("alexandre.mathieu.7@ulaval.ca"));
    private static final String A_MESSAGE = "An incredibly based email message!";

    public static void main(String[] args) {
        LogNotificationSender logNotificationSender = new LogNotificationSender();

        logNotificationSender.send(AN_ACCOUNT, A_MESSAGE);
    }
}
