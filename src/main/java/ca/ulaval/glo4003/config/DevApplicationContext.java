package ca.ulaval.glo4003.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.commons.api.exception.mapper.CatchallExceptionMapper;
import ca.ulaval.glo4003.commons.api.exception.mapper.CommonExceptionMapper;
import ca.ulaval.glo4003.commons.api.exception.mapper.ConstraintViolationExceptionMapper;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.identitymanagement.api.AuthResource;
import ca.ulaval.glo4003.identitymanagement.api.exception.IdentityManagementExceptionMapper;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenGenerator;
import ca.ulaval.glo4003.identitymanagement.infrastructure.CryptPasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.infrastructure.InMemoryUserRepository;
import ca.ulaval.glo4003.identitymanagement.infrastructure.JWTTokenGenerator;
import ca.ulaval.glo4003.repul.api.HealthResource;
import ca.ulaval.glo4003.repul.api.exception.mapper.RepULExceptionMapper;
import ca.ulaval.glo4003.repul.api.subscription.SubscriptionResource;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.IngredientInformation;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.http.CORSResponseFilter;
import ca.ulaval.glo4003.repul.infrastructure.EmulatedPaymentHandler;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryRepULRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DevApplicationContext implements ApplicationContext {
    private static final String CAMPUS_STATIONS_LOCATION_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final String LOCATION_FIELD_NAME_IN_JSON = "location";
    private static final String NAME_FIELD_NAME_IN_JSON = "name";
    private static final String CAPACITY_FIELD_NAME_IN_JSON = "capacity";
    private static final String SEMESTERS_FILE_PATH = "src/main/resources/semesters-232425.json";
    private static final String INGREDIENTS_FILE_PATH = "src/main/resources/ingredients.csv";
    private static final String SEMESTER_CODE_FIELD_NAME_IN_JSON = "semester_code";
    private static final String START_DATE_FIELD_NAME_IN_JSON = "start_date";
    private static final String END_DATE_FIELD_NAME_IN_JSON = "end_date";
    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);

    private static HealthResource createHealthResource() {
        LOGGER.info("Setup health resource");

        return new HealthResource();
    }

    private static AuthResource createAuthResource(UniqueIdentifierFactory uniqueIdentifierFactory) {
        LOGGER.info("Setup auth resource");
        PasswordEncoder passwordEncoder = new CryptPasswordEncoder();
        TokenGenerator tokenGenerator = new JWTTokenGenerator();

        UserFactory userFactory = new UserFactory(passwordEncoder);

        UserRepository userRepository = new InMemoryUserRepository();

        AuthService authService = new AuthService(userRepository, userFactory, uniqueIdentifierFactory, tokenGenerator);

        return new AuthResource(authService);
    }

    private static SubscriptionResource createSubscriptionResource() {
        LOGGER.info("Setup subscription resource");
        PaymentHandler paymentHandler = new EmulatedPaymentHandler();

        SubscriptionService subscriptionService = new SubscriptionService(paymentHandler);

        return new SubscriptionResource(subscriptionService);
    }

    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        UniqueIdentifierFactory uniqueIdentifierFactory = new UniqueIdentifierFactory();

        RepULRepository repULRepository = new InMemoryRepULRepository();

        initializeRepUL(repULRepository);

        LOGGER.info("Setup resources (API)");
        HealthResource healthResource = createHealthResource();
        SubscriptionResource subscriptionResource = createSubscriptionResource();
        AuthResource authResource = createAuthResource(uniqueIdentifierFactory);

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
                bind(authResource).to(AuthResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003").property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true).register(binder)
            .register(new CORSResponseFilter()).register(new IdentityManagementExceptionMapper()).register(new CatchallExceptionMapper())
            .register(new RepULExceptionMapper()).register(new CommonExceptionMapper()).register(new ConstraintViolationExceptionMapper());
    }

    public void initializeRepUL(RepULRepository repULRepository) {
        Catalog catalog = createCatalog();
        RepUL repUL = new RepUL(catalog);
        repULRepository.saveOrUpdate(repUL);
    }

    private Catalog createCatalog() {
        LOGGER.info("Setup catalog");
        List<PickupLocation> pickupLocations = parsePickupLocations();
        List<Semester> semesters = parseSemesters();
        List<IngredientInformation> ingredientInformations = parseIngredientInformation();
        return new Catalog(pickupLocations, semesters, ingredientInformations);
    }

    private List<PickupLocation> parsePickupLocations() {
        List<Map<String, Object>> listOfLocationMaps = getListOfMapsFromJsonFilePath(CAMPUS_STATIONS_LOCATION_FILE_PATH);
        return listOfLocationMaps.stream().map(
            map -> new PickupLocation(new LocationId((String) map.get(LOCATION_FIELD_NAME_IN_JSON)), (String) map.get(NAME_FIELD_NAME_IN_JSON),
                (int) map.get(CAPACITY_FIELD_NAME_IN_JSON))).toList();
    }

    private List<Semester> parseSemesters() {
        List<Map<String, Object>> listOfSemesterMaps = getListOfMapsFromJsonFilePath(SEMESTERS_FILE_PATH);
        return listOfSemesterMaps.stream().map(map -> new Semester(new SemesterCode((String) map.get(SEMESTER_CODE_FIELD_NAME_IN_JSON)),
            parseDate((String) map.get(START_DATE_FIELD_NAME_IN_JSON)), parseDate((String) map.get(END_DATE_FIELD_NAME_IN_JSON)))).toList();
    }

    private List<IngredientInformation> parseIngredientInformation() {
        List<IngredientInformation> ingredients = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INGREDIENTS_FILE_PATH))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                ingredients.add(new IngredientInformation(values[0], new Amount(Double.parseDouble(values[1]))));
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading " + INGREDIENTS_FILE_PATH, e);
            throw new RuntimeException("Error while reading " + INGREDIENTS_FILE_PATH);
        }
        return ingredients;
    }

    private List<Map<String, Object>> getListOfMapsFromJsonFilePath(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> listOfMaps = objectMapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>() {
            });
            return listOfMaps;
        } catch (IOException e) {
            LOGGER.error("Error while reading " + filePath, e);
            throw new RuntimeException("Error while reading " + filePath);
        }
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (java.text.ParseException e) {
            LOGGER.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date");
        }
    }
}
