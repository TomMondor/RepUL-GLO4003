package ca.ulaval.glo4003.repul.application.order;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;

public class OrderService {
    private final RepULRepository repULRepository;

    public OrderService(RepULRepository repULRepository) {
        this.repULRepository = repULRepository;
    }

    public OrdersPayload getAccountCurrentOrders(UniqueIdentifier accountId) {
        RepUL repUL = repULRepository.get();
        return new OrdersPayload(repUL.getAccountCurrentOrders(accountId));
    }
}
