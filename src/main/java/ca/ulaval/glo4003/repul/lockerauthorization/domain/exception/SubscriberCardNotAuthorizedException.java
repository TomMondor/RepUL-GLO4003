package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

public class SubscriberCardNotAuthorizedException extends LockerAuthorizationException {
    public SubscriberCardNotAuthorizedException() {
        super("This card is not authorized to open this locker.");
    }
}
