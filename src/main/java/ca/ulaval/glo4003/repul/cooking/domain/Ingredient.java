package ca.ulaval.glo4003.repul.cooking.domain;

public record Ingredient(
    String name,
    Quantity quantity
) {
    public Ingredient add(Quantity quantity) {
        return new Ingredient(name, this.quantity.add(quantity));
    }
}
