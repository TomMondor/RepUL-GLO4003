package ca.ulaval.glo4003.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.api.HealthResource;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.http.CORSResponseFilter;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryRepULRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DevApplicationContext implements ApplicationContext {
    private static final String CATALOG_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);

    private static HealthResource createHealthResource() {
        LOGGER.info("Setup health resource");

        return new HealthResource();
    }

    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        RepULRepository repULRepository = new InMemoryRepULRepository();
        initializeRepUL(repULRepository);

        LOGGER.info("Setup resources (API)");
        HealthResource healthResource = createHealthResource();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> listOfMaps = objectMapper.readValue(new File(CATALOG_FILE_PATH), new TypeReference<List<Map<String, Object>>>() {
            });
            List<PickupLocation> pickupLocations = listOfMaps.stream()
                .map(map -> new PickupLocation(new LocationId((String) map.get("location")), (String) map.get("name"), (int) map.get("capacity")))
                .collect(java.util.stream.Collectors.toList());
            return new Catalog(pickupLocations);
        } catch (IOException e) {
            LOGGER.error("Error while reading " + CATALOG_FILE_PATH, e);
            throw new RuntimeException("Error while reading campus-stations-location.json");
        }
    }
}
