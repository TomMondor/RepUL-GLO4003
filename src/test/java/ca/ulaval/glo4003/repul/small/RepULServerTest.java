package ca.ulaval.glo4003.repul.small;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.RepULServer;
import ca.ulaval.glo4003.repul.fixture.FakeHeartbeatContextFixture;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepULServerTest {
    private final FakeHeartbeatContextFixture fakeHeartbeatContextFixture = new FakeHeartbeatContextFixture();
    private final RepULServer repULServer = new RepULServer(fakeHeartbeatContextFixture);

    @Test
    public void givenApplicationContext_whenRunningServer_shouldRun() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.submit(repULServer);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // The thread has been interrupted
        }

        Response response = when().get(fakeHeartbeatContextFixture.getURI() + "api/heartbeat");
        assertEquals(200, response.getStatusCode());
    }
}
