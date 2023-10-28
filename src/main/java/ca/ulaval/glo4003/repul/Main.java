package ca.ulaval.glo4003.repul;

import java.util.Arrays;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.DemoApplicationContext;
import ca.ulaval.glo4003.repul.config.DevApplicationContext;
import ca.ulaval.glo4003.repul.config.TestApplicationContext;

/**
 * RESTApi setup without using DI or spring.
 */
@SuppressWarnings("all")
public class Main {
    private static final String TEST_COMMAND = "--test";
    private static final String DEMO_COMMAND = "--demo";

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = getApplicationContext(args);
        Runnable server = new RepULServer(applicationContext);
        server.run();
    }

    private static ApplicationContext getApplicationContext(String[] args) {
        if (Arrays.stream(args).toList().contains(TEST_COMMAND)) {
            return new TestApplicationContext();
        }
        if (Arrays.stream(args).toList().contains(DEMO_COMMAND)) {
            return new DemoApplicationContext();
        }
        return new DevApplicationContext();
    }
}
