package ca.ulaval.glo4003.repul.cooking.api.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class SelectionRequest {
    @NotNull(message = "The meal kit ids may not be null.")
    public List<String> ids;
}
