package ca.ulaval.glo4003.repul.small.lockerauthorization.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationResource;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LockerAuthorizationResourceTest {
    private static final String A_CARD_NUMBER = "111222333";
    private static final String A_DELIVERY_LOCATION_ID = "1";
    private static final int A_LOCKER_NUMBER = 10;

    @Mock
    private LockerAuthorizationService lockerAuthorizationService;

    private LockerAuthorizationResource lockerAuthorizationResource;

    @BeforeEach
    public void createLockerAuthorizationResource() {
        lockerAuthorizationResource = new LockerAuthorizationResource(lockerAuthorizationService);
    }

    @Test
    public void whenOpening_shouldAuthorizeLocker() {
        OpenLockerQuery query = OpenLockerQuery.from(A_CARD_NUMBER, A_DELIVERY_LOCATION_ID, A_LOCKER_NUMBER);

        lockerAuthorizationResource.openLocker(getLockerOpenRequest());

        verify(lockerAuthorizationService).openLocker(query);
    }

    @Test
    public void whenOpening_shouldReturn204() {
        Response response = lockerAuthorizationResource.openLocker(getLockerOpenRequest());

        assertEquals(response.getStatus(), 204);
    }

    private OpenLockerRequest getLockerOpenRequest() {
        OpenLockerRequest request = new OpenLockerRequest();
        request.userCardNumber = A_CARD_NUMBER;
        request.deliveryLocationId = A_DELIVERY_LOCATION_ID;
        request.lockerNumber = A_LOCKER_NUMBER;
        return request;
    }
}
