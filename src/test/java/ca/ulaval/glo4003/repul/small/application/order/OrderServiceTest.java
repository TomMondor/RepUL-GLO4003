package ca.ulaval.glo4003.repul.small.application.order;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.order.OrderService;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.fixture.OrderFixture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = new UniqueIdentifier(UUID.randomUUID());
    private static final Order AN_ORDER = new OrderFixture().build();
    @Mock
    RepULRepository mockRepULRepository;
    private OrderService orderService;
    @Mock
    private RepUL mockRepUL;

    @BeforeEach
    public void createOrderService() {
        when(mockRepULRepository.get()).thenReturn(mockRepUL);
        orderService = new OrderService(mockRepULRepository);
    }

    @Test
    public void whenGettingAccountOrders_shouldGetRepUL() {
        when(mockRepUL.getAccountCurrentOrders(any())).thenReturn(List.of(AN_ORDER));

        orderService.getAccountCurrentOrders(A_UNIQUE_IDENTIFIER);

        verify(mockRepULRepository).get();
    }

    @Test
    public void whenGettingAccountOrders_shouldGetAccountOrders() {
        when(mockRepUL.getAccountCurrentOrders(any())).thenReturn(List.of(AN_ORDER));

        orderService.getAccountCurrentOrders(A_UNIQUE_IDENTIFIER);

        verify(mockRepUL).getAccountCurrentOrders(A_UNIQUE_IDENTIFIER);
    }
}
