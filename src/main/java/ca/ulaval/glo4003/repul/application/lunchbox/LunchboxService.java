package ca.ulaval.glo4003.repul.application.lunchbox;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;

public class LunchboxService {
    private final RepULRepository repULRepository;

    public LunchboxService(RepULRepository repULRepository) {
        this.repULRepository = repULRepository;
    }

    public List<Lunchbox> getLunchboxesToCook() {
        RepUL repUL = repULRepository.get();
        return repUL.getLunchboxesToCook();
    }
}
