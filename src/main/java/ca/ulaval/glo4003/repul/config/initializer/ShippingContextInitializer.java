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
import ca.ulaval.glo4003.repul.shipping.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.shipping.application.ShippingService;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.shipping.infrastructure.InMemoryShippingRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShippingContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShippingContextInitializer.class);
    private static final String CAMPUS_STATIONS_LOCATION_FILE_PATH = "src/main/resources/campus-stations-location.json";
    private static final String LOCATION_FIELD_NAME_IN_JSON = "location";
    private static final String NAME_FIELD_NAME_IN_JSON = "name";
    private static final String CAPACITY_FIELD_NAME_IN_JSON = "capacity";
    private List<UniqueIdentifier> deliveryPersonIds = new ArrayList<>();

    private ShippingRepository shippingRepository = new InMemoryShippingRepository();

    public ShippingContextInitializer withShippingRepository(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
        return this;
    }

    public ShippingContextInitializer withShippers(List<UniqueIdentifier> deliveryPersonIds) {
        this.deliveryPersonIds.addAll(deliveryPersonIds);
        return this;
    }

    public ShippingService createShippingService(RepULEventBus eventBus) {
        LOGGER.info("Creating Shipping service");
        initializeShipping(shippingRepository);
        return new ShippingService(shippingRepository, eventBus);
    }

    public LocationsCatalogService createLocationsCatalogService() {
        LOGGER.info("Creating Locations Catalog service");
        return new LocationsCatalogService(shippingRepository);
    }

    private void initializeShipping(ShippingRepository shippingRepository) {
        List<DeliveryLocation> deliveryLocations = parseShippingLocations();
        List<KitchenLocation> kitchenLocations = new ArrayList<>();
        kitchenLocations.add(new KitchenLocation(new KitchenLocationId("DESJARDINS"), "Desjardins"));

        LocationsCatalog locationsCatalog = new LocationsCatalog(deliveryLocations, kitchenLocations);

        Shipping shipping = new Shipping(locationsCatalog);
        deliveryPersonIds.forEach(shipping::addDeliveryPerson);

        shippingRepository.saveOrUpdate(shipping);
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
