package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.delivery.api.CargoResource;
import ca.ulaval.glo4003.repul.delivery.api.DeliveryEventHandler;
import ca.ulaval.glo4003.repul.delivery.api.LocationResource;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemPersister;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryContextInitializer implements ContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryContextInitializer.class);
    private static final String CAMPUS_STATIONS_LOCATION_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final String LOCATION_FIELD_NAME_IN_JSON = "location";
    private static final String NAME_FIELD_NAME_IN_JSON = "name";
    private static final String CAPACITY_FIELD_NAME_IN_JSON = "capacity";

    private final RepULEventBus eventBus;
    private final SeedFactory seedFactory;
    private final DeliverySystemPersister deliverySystemPersister = new InMemoryDeliverySystemPersister();

    public DeliveryContextInitializer(RepULEventBus eventBus, SeedFactory seedFactory) {
        this.eventBus = eventBus;
        this.seedFactory = seedFactory;
    }

    @Override
    public void initialize(ResourceConfig resourceConfig) {
        LocationsCatalog locationsCatalog = createLocationsCatalog();
        DeliveryService deliveryService = new DeliveryService(deliverySystemPersister, eventBus);
        createDeliveryEventHandler(deliveryService);
        populate(locationsCatalog);

        CargoResource cargoResource = new CargoResource(deliveryService);
        LocationResource locationResource = new LocationResource(new LocationsCatalogService(deliverySystemPersister));

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(cargoResource).to(CargoResource.class);
                bind(locationResource).to(LocationResource.class);
            }
        };

        resourceConfig.register(binder);
    }

    private LocationsCatalog createLocationsCatalog() {
        List<DeliveryLocation> deliveryLocations = parseDeliveryLocations();
        List<KitchenLocation> kitchenLocations = new ArrayList<>();
        kitchenLocations.add(new KitchenLocation(KitchenLocationId.DESJARDINS, "Desjardins"));

        return new LocationsCatalog(deliveryLocations, kitchenLocations);
    }

    private void createDeliveryEventHandler(DeliveryService deliveryService) {
        DeliveryEventHandler deliveryEventHandler = new DeliveryEventHandler(deliveryService);
        eventBus.register(deliveryEventHandler);
    }

    private void populate(LocationsCatalog locationsCatalog) {
        Seed seed = seedFactory.createDeliverySeed(deliverySystemPersister, locationsCatalog);
        seed.populate();
    }

    private List<DeliveryLocation> parseDeliveryLocations() {
        List<Map<String, Object>> listOfLocationMaps = getListOfMapsFromJsonFilePath(CAMPUS_STATIONS_LOCATION_FILE_PATH);
        List<DeliveryLocation> deliveryLocations = new ArrayList<>();

        for (Map<String, Object> locationAsMap : listOfLocationMaps) {
            String locationId = (String) locationAsMap.get(LOCATION_FIELD_NAME_IN_JSON);
            String name = (String) locationAsMap.get(NAME_FIELD_NAME_IN_JSON);
            int capacity = (int) locationAsMap.get(CAPACITY_FIELD_NAME_IN_JSON);

            DeliveryLocation deliveryLocation = new DeliveryLocation(DeliveryLocationId.valueOf(locationId), name, capacity);
            deliveryLocations.add(deliveryLocation);
        }

        return deliveryLocations;
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
