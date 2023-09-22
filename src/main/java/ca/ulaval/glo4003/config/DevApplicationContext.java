package ca.ulaval.glo4003.config;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.api.HealthResource;
import ca.ulaval.glo4003.repul.api.subscription.SubscriptionResource;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.http.CORSResponseFilter;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryRepULRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DevApplicationContext implements ApplicationContext {
    private static final String CAMPUS_STATIONS_LOCATION_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final String LOCATION_FIELD_NAME_IN_JSON = "location";
    private static final String NAME_FIELD_NAME_IN_JSON = "name";
    private static final String CAPACITY_FIELD_NAME_IN_JSON = "capacity";
    private static final String SEMESTERS_FILE_PATH = "src/main/resources/semesters-232425.json";
    private static final String SEMESTER_CODE_FIELD_NAME_IN_JSON = "semesterCode";
    private static final String START_DATE_FIELD_NAME_IN_JSON = "startDate";
    private static final String END_DATE_FIELD_NAME_IN_JSON = "endDate";
    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);

    private static HealthResource createHealthResource() {
        LOGGER.info("Setup health resource");

        return new HealthResource();
    }

    private static SubscriptionResource createSubscriptionResource(SubscriptionService subscriptionService) {
        return new SubscriptionResource(subscriptionService);
    }

    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        RepULRepository repULRepository = new InMemoryRepULRepository();
        initializeRepUL(repULRepository);

        SubscriptionService subscriptionService = new SubscriptionService();

        LOGGER.info("Setup resources (API)");
        HealthResource healthResource = createHealthResource();
        SubscriptionResource subscriptionResource = createSubscriptionResource(subscriptionService);

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.repul.api").register(binder).register(new CORSResponseFilter());
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
        return new Catalog(pickupLocations, semesters);
    }

    private List<PickupLocation> parsePickupLocations() {
        List<Map<String, Object>> listOfLocationMaps = getListOfMapsFromFilePath(CAMPUS_STATIONS_LOCATION_FILE_PATH);
        return listOfLocationMaps.stream()
            .map(map -> new PickupLocation(new LocationId((String) map.get(LOCATION_FIELD_NAME_IN_JSON)), (String) map.get(NAME_FIELD_NAME_IN_JSON),
                (int) map.get(CAPACITY_FIELD_NAME_IN_JSON))).toList();
    }

    private List<Semester> parseSemesters() {
        List<Map<String, Object>> listOfSemesterMaps = getListOfMapsFromFilePath(SEMESTERS_FILE_PATH);
        return listOfSemesterMaps.stream()
            .map(map -> new Semester(new SemesterCode((String) map.get(SEMESTER_CODE_FIELD_NAME_IN_JSON)),
                parseDate((String) map.get(START_DATE_FIELD_NAME_IN_JSON)),
                parseDate((String) map.get(END_DATE_FIELD_NAME_IN_JSON)))).toList();
    }

    private List<Map<String, Object>> getListOfMapsFromFilePath(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> listOfMaps =
                objectMapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>() {
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
