package ca.ulaval.glo4003.repul.api.subscription.response;

public record SubscriptionResponse(String subscriptionId, String dayOfWeek, String locationId, String lunchboxType, String startDate, String semesterCode) {
}
