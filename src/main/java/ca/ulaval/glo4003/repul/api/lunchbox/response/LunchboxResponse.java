package ca.ulaval.glo4003.repul.api.lunchbox.response;

import java.util.List;

public record LunchboxResponse(String lunchboxType, List<String> recipes) {
}
