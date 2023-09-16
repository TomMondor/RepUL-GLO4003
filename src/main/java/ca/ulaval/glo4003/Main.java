package ca.ulaval.glo4003;

import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.ProductionApplicationContext;

/**
 * RESTApi setup without using DI or spring.
 */
@SuppressWarnings("all")
public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ProductionApplicationContext();

        Runnable server = new RepULServer(applicationContext);
        server.run();
    }
}
