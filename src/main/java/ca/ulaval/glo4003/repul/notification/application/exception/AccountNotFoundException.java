package ca.ulaval.glo4003.repul.notification.application.exception;

public class AccountNotFoundException extends NotificationException {
    public AccountNotFoundException() {
        super("The given account was not found.");
    }
}
