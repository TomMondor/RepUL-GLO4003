package ca.ulaval.glo4003;

import java.util.Arrays;

import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DemoApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;

/**
 * RESTApi setup without using DI or spring.
 */
@SuppressWarnings("all")
public class Main {
    private static final String DEMO_COMMAND = "--demo";

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = getApplicationContext(args);
        Runnable server = new RepULServer(applicationContext);
        server.run();
    }

    private static ApplicationContext getApplicationContext(String[] args) {
        if (Arrays.stream(args).toList().contains(DEMO_COMMAND)) {
            return new DemoApplicationContext();
        }
        return new DevApplicationContext();
    }
}
