package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.config.initializer.jobs.JobInitializer;
import ca.ulaval.glo4003.repul.config.initializer.jobs.ProcessOrdersJobInitializer;
import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.subscription.api.AccountResource;
import ca.ulaval.glo4003.repul.subscription.api.SubscriberEventHandler;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscriptionContextInitializer implements ContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionContextInitializer.class);
    private static final String SEMESTERS_FILE_PATH = "src/main/resources/semesters-232425.json";
    private static final String SEMESTER_CODE_FIELD_NAME_IN_JSON = "semester_code";
    private static final String START_DATE_FIELD_NAME_IN_JSON = "start_date";
    private static final String END_DATE_FIELD_NAME_IN_JSON = "end_date";
    private final RepULEventBus eventBus;
    private final PaymentService paymentService;
    private final SeedFactory seedFactory;
    private final SubscriptionFactory subscriptionFactory;
    private final OrdersFactory ordersFactory;
    private final String cronFrequency;
    private final SubscriberFactory subscriberFactory = new SubscriberFactory();
    private final SubscriberRepository subscriberRepository = new InMemorySubscriberRepository();

    public SubscriptionContextInitializer(RepULEventBus eventBus, PaymentService paymentService, SeedFactory seedFactory, String cronFrequency) {
        List<Semester> semesters = parseSemesters();
        List<DeliveryLocationId> deliveryLocationIds = new ArrayList<>(EnumSet.allOf(DeliveryLocationId.class));

        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class);

        this.subscriptionFactory = new SubscriptionFactory(subscriptionUniqueIdentifierFactory, ordersFactory, semesters, deliveryLocationIds);
        this.eventBus = eventBus;
        this.cronFrequency = cronFrequency;
        this.paymentService = paymentService;
        this.seedFactory = seedFactory;
    }

    @Override
    public void initialize(ResourceConfig resourceConfig) {
        SubscriberService service =
            new SubscriberService(subscriberRepository, subscriberFactory, subscriptionFactory, eventBus, paymentService, ordersFactory);
        createSubscriberEventHandler(service);
        populate();
        initializeCron(service);

        AccountResource accountResource = new AccountResource(service);
        SubscriptionResource subscriptionResource = new SubscriptionResource(service);

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(accountResource).to(AccountResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
            }
        };

        resourceConfig.register(binder);
    }

    private void createSubscriberEventHandler(SubscriberService subscriberService) {
        SubscriberEventHandler subscriberEventHandler = new SubscriberEventHandler(subscriberService);
        eventBus.register(subscriberEventHandler);
    }

    private void populate() {
        Seed seed = seedFactory.createSubscriptionSeed(subscriberRepository);
        seed.populate();
    }

    private void initializeCron(SubscriberService subscriberService) {
        JobInitializer processOrdersJob = new ProcessOrdersJobInitializer(subscriberService, cronFrequency);
        processOrdersJob.launchJob();
    }

    private List<Semester> parseSemesters() {
        List<Map<String, Object>> listOfSemesterMaps = getListOfMapsFromJsonFilePath(SEMESTERS_FILE_PATH);
        List<Semester> semesters = new ArrayList<>();

        for (Map<String, Object> semesterAsMap : listOfSemesterMaps) {
            String semesterCode = (String) semesterAsMap.get(SEMESTER_CODE_FIELD_NAME_IN_JSON);
            LocalDate startDate = parseDate((String) semesterAsMap.get(START_DATE_FIELD_NAME_IN_JSON));
            LocalDate endDate = parseDate((String) semesterAsMap.get(END_DATE_FIELD_NAME_IN_JSON));

            Semester semester = new Semester(new SemesterCode(semesterCode), startDate, endDate);
            semesters.add(semester);
        }
        return semesters;
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            LOGGER.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date");
        }
    }

    private List<Map<String, Object>> getListOfMapsFromJsonFilePath(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(filePath), new TypeReference<>() {
            });
        } catch (IOException e) {
            LOGGER.error("Error while reading " + filePath, e);
            throw new RuntimeException("Error while reading " + filePath);
        }
    }
}
