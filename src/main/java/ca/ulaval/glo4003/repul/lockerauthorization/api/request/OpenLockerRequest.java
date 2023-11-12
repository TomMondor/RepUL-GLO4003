package ca.ulaval.glo4003.repul.lockerauthorization.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLockerRequest {
    @NotNull(message = "The user card number may not be null.")
    public String userCardNumber;

    @NotNull(message = "The delivery location ID may not be null.")
    public String deliveryLocationId;

    @NotNull(message = "The locker number may not be null.")
    public int lockerNumber;
}
