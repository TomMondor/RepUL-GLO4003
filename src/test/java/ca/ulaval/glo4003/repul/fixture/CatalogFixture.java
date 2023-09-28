package ca.ulaval.glo4003.repul.fixture;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.IngredientInformation;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;

public class CatalogFixture {
    private List<PickupLocation> pickupLocations = new ArrayList();
    private List<Semester> semesters = new ArrayList();
    private List<IngredientInformation> ingredients = new ArrayList();
    private Lunchbox standardLunchbox;

    public CatalogFixture() {
        pickupLocations.add(new PickupLocation(new LocationId("here"), "over there", 10));
        semesters.add(new Semester(new SemesterCode("H25"), LocalDate.now().minusDays(30), LocalDate.now().plusDays(60)));
        ingredients.add(new IngredientInformation("apple", new Amount(5.00)));
        standardLunchbox = new LunchboxFixture().build();
    }

    public CatalogFixture addPickupLocation(PickupLocation pickupLocation) {
        pickupLocations.add(pickupLocation);
        return this;
    }

    public CatalogFixture withPickupLocations(List<PickupLocation> pickupLocations) {
        this.pickupLocations = pickupLocations;
        return this;
    }

    public CatalogFixture addSemester(Semester semester) {
        semesters.add(semester);
        return this;
    }

    public CatalogFixture withSemesters(List<Semester> semesters) {
        this.semesters = semesters;
        return this;
    }

    public CatalogFixture addIngredient(IngredientInformation ingredient) {
        ingredients.add(ingredient);
        return this;
    }

    public CatalogFixture withIngredients(List<IngredientInformation> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public CatalogFixture withStandardLunchbox(Lunchbox standardLunchbox) {
        this.standardLunchbox = standardLunchbox;
        return this;
    }

    public Catalog build() {
        return new Catalog(pickupLocations, semesters, ingredients, standardLunchbox);
    }
}
