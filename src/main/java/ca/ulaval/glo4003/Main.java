package ca.ulaval.glo4003;

import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.ProductionApplicationContext;
import ca.ulaval.glo4003.config.SubscriptionServer;

/**
 * RESTApi setup without using DI or spring.
 */
@SuppressWarnings("all")
public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ProductionApplicationContext();

        Runnable server = new SubscriptionServer(applicationContext);
        server.run();
    }
}
