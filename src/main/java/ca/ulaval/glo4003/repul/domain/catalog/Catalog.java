package ca.ulaval.glo4003.repul.domain.catalog;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationException;

public class Catalog {
    private final Map<LocationId, PickupLocation> pickupLocations;
    private final Map<SemesterCode, Semester> semesters;
    private final Map<String, Amount> ingredientPrices;

    public Catalog(List<PickupLocation> pickupLocations, List<Semester> semesters, List<IngredientInformation> ingredients) {
        this.pickupLocations = pickupLocations.stream().collect(Collectors.toMap(PickupLocation::getLocationId, Function.identity()));
        this.semesters = semesters.stream().collect(Collectors.toMap(Semester::semesterCode, Function.identity()));
        this.ingredientPrices = ingredients.stream().collect(Collectors.toMap(IngredientInformation::name, IngredientInformation::price));
    }

    public void validateLocationId(LocationId locationId) {
        if (!pickupLocations.containsKey(locationId)) {
            throw new InvalidLocationException();
        }
    }
}
