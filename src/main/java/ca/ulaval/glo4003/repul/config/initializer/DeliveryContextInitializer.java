package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryContextInitializer.class);
    private static final String CAMPUS_STATIONS_LOCATION_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final String LOCATION_FIELD_NAME_IN_JSON = "location";
    private static final String NAME_FIELD_NAME_IN_JSON = "name";
    private static final String CAPACITY_FIELD_NAME_IN_JSON = "capacity";
    private final List<UniqueIdentifier> deliveryPersonIds = new ArrayList<>();

    private DeliverySystemRepository deliverySystemRepository = new InMemoryDeliverySystemRepository();

    public DeliveryContextInitializer withShippingRepository(DeliverySystemRepository deliverySystemRepository) {
        this.deliverySystemRepository = deliverySystemRepository;
        return this;
    }

    public DeliveryContextInitializer withDeliveryPeople(List<UniqueIdentifier> deliveryPersonIds) {
        this.deliveryPersonIds.addAll(deliveryPersonIds);
        return this;
    }

    public DeliveryService createDeliveryService(RepULEventBus eventBus) {
        LOGGER.info("Creating Delivery service");
        initializeDelivery(deliverySystemRepository);
        return new DeliveryService(deliverySystemRepository, eventBus);
    }

    public LocationsCatalogService createLocationsCatalogService() {
        LOGGER.info("Creating Locations Catalog service");
        return new LocationsCatalogService(deliverySystemRepository);
    }

    private void initializeDelivery(DeliverySystemRepository deliverySystemRepository) {
        List<DeliveryLocation> deliveryLocations = parseShippingLocations();
        List<KitchenLocation> kitchenLocations = new ArrayList<>();
        kitchenLocations.add(new KitchenLocation(new KitchenLocationId("DESJARDINS"), "Desjardins"));

        LocationsCatalog locationsCatalog = new LocationsCatalog(deliveryLocations, kitchenLocations);

        DeliverySystem deliverySystem = new DeliverySystem(locationsCatalog);
        deliveryPersonIds.forEach(deliverySystem::addDeliveryPerson);

        deliverySystemRepository.saveOrUpdate(deliverySystem);
    }

    private List<DeliveryLocation> parseShippingLocations() {
        List<Map<String, Object>> listOfLocationMaps = getListOfMapsFromJsonFilePath(CAMPUS_STATIONS_LOCATION_FILE_PATH);
        return listOfLocationMaps.stream().map(
            map -> new DeliveryLocation(new DeliveryLocationId((String) map.get(LOCATION_FIELD_NAME_IN_JSON)), (String) map.get(NAME_FIELD_NAME_IN_JSON),
                (int) map.get(CAPACITY_FIELD_NAME_IN_JSON))).toList();
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
