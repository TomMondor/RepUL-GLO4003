package ca.ulaval.glo4003.repul.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.domain.Exception.RepULNotFoundException;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;

public class InMemoryRepULRepository implements RepULRepository {
    private Optional<RepUL> repUL = Optional.empty();

    @Override
    public void saveOrUpdate(RepUL repUL) {
        this.repUL = Optional.of(repUL);
    }

    @Override
    public RepUL get() {
        return repUL.orElseThrow(RepULNotFoundException::new);
    }
}
