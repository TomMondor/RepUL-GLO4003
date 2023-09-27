package ca.ulaval.glo4003.repul.small.domain.catalog;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.IngredientInformation;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.domain.exception.SemesterNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogTest {
    private static final String A_LOCATION_ID = "a location id";
    private static final String A_MISSING_LOCATION_ID = "a missing location id";
    private static final String A_NAME = "a name";
    private static final int A_TOTAL_CAPACITY = 10;
    private static final String ANOTHER_LOCATION_ID = "another location id";
    private static final String ANOTHER_NAME = "another name";
    private static final int ANOTHER_TOTAL_CAPACITY = 20;
    private static final String A_SEMESTER_CODE = "E24";
    private static final LocalDate A_START_DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate AN_END_DATE = LocalDate.of(2020, 4, 20);
    private static final LocalDate A_DATE_IN_SEMESTER = LocalDate.of(2020, 2, 15);
    private static final LocalDate A_DATE_NOT_IN_SEMESTER = LocalDate.of(2020, 9, 1);
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(new LocationId(A_LOCATION_ID), A_NAME, A_TOTAL_CAPACITY);
    private static final PickupLocation ANOTHER_PICKUP_LOCATION = new PickupLocation(new LocationId(ANOTHER_LOCATION_ID), ANOTHER_NAME, ANOTHER_TOTAL_CAPACITY);
    private static final String AN_INGREDIENT_NAME = "an ingredient name";
    private static final Amount AN_INGREDIENT_PRICE = new Amount(1.0);
    private static final String A_RECIPE_NAME = "a recipe name";
    private static final int A_RECIPE_CALORIES = 100;
    private static final double AN_INGREDIENT_QUANTITY_VALUE = 1.0;
    private static final String AN_INGREDIENT_QUANTITY_UNIT = "mg";
    private static final Semester A_SEMESTER = new Semester(new SemesterCode(A_SEMESTER_CODE), A_START_DATE, AN_END_DATE);

    private Catalog catalog;

    @BeforeEach
    public void setUpCatalog() {
        List<PickupLocation> pickupLocations = List.of(A_PICKUP_LOCATION, ANOTHER_PICKUP_LOCATION);
        List<Semester> semesters = List.of(A_SEMESTER);
        List<IngredientInformation> ingredients = List.of(new IngredientInformation(AN_INGREDIENT_NAME, AN_INGREDIENT_PRICE));
        Lunchbox standardLunchbox = new Lunchbox(List.of(new Recipe(A_RECIPE_NAME, A_RECIPE_CALORIES,
            List.of(new Ingredient(AN_INGREDIENT_NAME, new Quantity(AN_INGREDIENT_QUANTITY_VALUE, AN_INGREDIENT_QUANTITY_UNIT))))));
        catalog = new Catalog(pickupLocations, semesters, ingredients, standardLunchbox);
    }

    @Test
    public void givenExistingLocationId_whenGetPickupLocation_returnPickupLocation() {
        PickupLocation pickupLocation = catalog.getPickupLocation(new LocationId(A_LOCATION_ID));

        assertEquals(A_PICKUP_LOCATION, pickupLocation);
    }

    @Test
    public void givenMissingLocationId_whenGetPickupLocation_shouldThrowInvalidLocationException() {
        assertThrows(InvalidLocationException.class, () -> catalog.getPickupLocation(new LocationId(A_MISSING_LOCATION_ID)));
    }

    @Test
    public void givenDateInSemester_whenGetSemester_shouldReturnSemester() {
        Semester semester = catalog.getCurrentSemester(A_DATE_IN_SEMESTER);

        assertEquals(semester, A_SEMESTER);
    }

    @Test
    public void givenDateNotInSemester_whenGetSemester_shouldThrowSemesterNotFoundException() {
        assertThrows(SemesterNotFoundException.class, () -> catalog.getCurrentSemester(A_DATE_NOT_IN_SEMESTER));
    }
}

