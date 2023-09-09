package ca.ulaval.glo4003;

import ca.ulaval.glo4003.ws.api.contact.ContactResource;
import ca.ulaval.glo4003.ws.api.contact.ContactResourceImpl;
import ca.ulaval.glo4003.ws.domain.contact.Contact;
import ca.ulaval.glo4003.ws.domain.contact.ContactAssembler;
import ca.ulaval.glo4003.ws.domain.contact.ContactRepository;
import ca.ulaval.glo4003.ws.domain.contact.ContactService;
import ca.ulaval.glo4003.ws.http.CORSResponseFilter;
import ca.ulaval.glo4003.ws.infrastructure.contact.ContactDevDataFactory;
import ca.ulaval.glo4003.ws.infrastructure.contact.ContactRepositoryInMemory;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * RESTApi setup without using DI or spring
 */
@SuppressWarnings("all")
public class TelephonyWsMain {
  private static final Logger LOGGER = LoggerFactory.getLogger(TelephonyWsMain.class);

  public static boolean isDev = true; // Would be a JVM argument or in a .property file
  public static final String BASE_URI = "http://localhost:8080/";

  public static void main(String[] args) throws Exception {

    LOGGER.info("Setup resources (API)");
    ContactResource contactResource = createContactResource();
    // TODO Something to do here!

    final AbstractBinder binder = new AbstractBinder() {
      @Override
      protected void configure() {
        bind(contactResource).to(ContactResource.class);
      }
    };

    final ResourceConfig config = new ResourceConfig();
    config.register(binder);
    config.register(new CORSResponseFilter());
    config.packages("ca.ulaval.glo4003.ws.api");

    try {
      LOGGER.info("Setup http server");
      final Server server = JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
          LOGGER.info("Shutting down the application...");
          server.stop();
          LOGGER.info("Done, exit.");
        } catch (Exception e) {
          LOGGER.error("Error shutting down the server", e);
        }
      }));

      LOGGER.info("Application started.%nStop the application using CTRL+C");

      // block and wait shut down signal, like CTRL+C
      Thread.currentThread().join();

    } catch (InterruptedException e) {
      LOGGER.error("Error startig up the server", e);
    }
  }

  private static ContactResource createContactResource() {
    LOGGER.info("Setup contact resource dependencies (DOMAIN + INFRASTRUCTURE)");
    ContactRepository contactRepository = new ContactRepositoryInMemory();

    // For development ease
    if (isDev) {
      ContactDevDataFactory contactDevDataFactory = new ContactDevDataFactory();
      List<Contact> contacts = contactDevDataFactory.createMockData();
      contacts.stream().forEach(contactRepository::save);
    }

    ContactAssembler contactAssembler = new ContactAssembler();
    ContactService contactService = new ContactService(contactRepository, contactAssembler);

    return new ContactResourceImpl(contactService);
  }

  private static void createCallLogResource() {
    // TODO Something to do here!
  }
}
