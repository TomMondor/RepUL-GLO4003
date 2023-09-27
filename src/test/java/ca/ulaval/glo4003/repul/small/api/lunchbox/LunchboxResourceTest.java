package ca.ulaval.glo4003.repul.small.api.lunchbox;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.lunchbox.LunchboxResource;
import ca.ulaval.glo4003.repul.application.lunchbox.LunchboxService;
import ca.ulaval.glo4003.repul.application.lunchbox.dto.LunchboxesPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LunchboxResourceTest {
    private static final LunchboxesPayload A_LUNCHBOXES_PAYLOAD = new LunchboxesPayload(Map.of(LunchboxType.STANDARD, new LunchboxFixture().build()));
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = new UniqueIdentifier(UUID.randomUUID());
    private LunchboxResource lunchboxResource;
    @Mock
    private LunchboxService lunchboxService;
    @Mock
    private ContainerRequestContext context;

    @BeforeEach
    public void createLunchboxResource() {
        lunchboxResource = new LunchboxResource(lunchboxService);
        given(context.getProperty(any())).willReturn(A_UNIQUE_IDENTIFIER);
        given(lunchboxService.getLunchboxes(any())).willReturn(A_LUNCHBOXES_PAYLOAD);
    }

    @Test
    public void whenGettingMyLunchboxes_shouldGetAccountId() {
        lunchboxResource.getMyLunchboxes(context);

        verify(context).getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
    }

    @Test
    public void whenGettingMyLunchboxes_shouldGetLunchboxes() {
        lunchboxResource.getMyLunchboxes(context);

        verify(lunchboxService).getLunchboxes(A_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenRegistering_shouldReturn200() {
        Response response = lunchboxResource.getMyLunchboxes(context);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
