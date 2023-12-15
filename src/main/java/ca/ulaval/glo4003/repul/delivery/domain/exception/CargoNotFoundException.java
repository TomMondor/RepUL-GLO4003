package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class CargoNotFoundException extends DeliveryException {
    public CargoNotFoundException() {
        super("The cargo with the given id was not found.");
    }
}
