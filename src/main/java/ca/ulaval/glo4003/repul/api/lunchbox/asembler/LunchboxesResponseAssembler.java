package ca.ulaval.glo4003.repul.api.lunchbox.asembler;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.api.lunchbox.response.LunchboxResponse;
import ca.ulaval.glo4003.repul.application.lunchbox.dto.LunchboxesPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;

public class LunchboxesResponseAssembler {
    public List<LunchboxResponse> toLunchboxesResponse(LunchboxesPayload lunchboxesDTO) {
        List<LunchboxResponse> lunchboxResponses = new ArrayList<>();
        lunchboxesDTO.lunchboxes().forEach((lunchboxType, lunchbox) -> {
            lunchboxResponses.add(new LunchboxResponse(lunchboxType.toString(), lunchbox.recipes().stream().map(Recipe::name).toList()));
        });
        return lunchboxResponses;
    }
}
