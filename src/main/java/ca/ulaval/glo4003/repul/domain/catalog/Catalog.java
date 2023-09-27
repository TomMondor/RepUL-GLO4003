package ca.ulaval.glo4003.repul.domain.catalog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.domain.exception.SemesterNotFoundException;

public class Catalog {
    private final Map<LocationId, PickupLocation> pickupLocations;
    private final Map<SemesterCode, Semester> semesters;
    private final Map<String, Amount> ingredientPrices;
    private final Lunchbox standardLunchbox;

    public Catalog(List<PickupLocation> pickupLocations, List<Semester> semesters, List<IngredientInformation> ingredients, Lunchbox standardLunchbox) {
        this.pickupLocations = pickupLocations.stream().collect(Collectors.toMap(PickupLocation::getLocationId, Function.identity()));
        this.semesters = semesters.stream().collect(Collectors.toMap(Semester::semesterCode, Function.identity()));
        this.ingredientPrices = ingredients.stream().collect(Collectors.toMap(IngredientInformation::name, IngredientInformation::price));
        this.standardLunchbox = standardLunchbox;
    }

    public PickupLocation getPickupLocation(LocationId locationId) {
        if (pickupLocations.containsKey(locationId)) {
            return pickupLocations.get(locationId);
        }
        throw new InvalidLocationException();
    }

    public Lunchbox getLunchbox() {
        return standardLunchbox;
    }

    public Semester getCurrentSemester(LocalDate currentDate) {
        List<Semester> savedSemesters = new ArrayList<>(semesters.values());
        return savedSemesters.stream()
            .filter(semester -> semester.startDate().isBefore(currentDate) && semester.endDate().isAfter(currentDate))
            .findFirst().orElseThrow(SemesterNotFoundException::new);
    }

    public List<PickupLocation> getPickupLocations() {
        return pickupLocations.values().stream().toList();
    }
}
