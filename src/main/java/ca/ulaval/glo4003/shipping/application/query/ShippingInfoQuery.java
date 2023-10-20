package ca.ulaval.glo4003.shipping.application.query;

import java.util.List;

public record ShippingInfoQuery(String pickupLocationId, List<MealkitShippingInfoQuery> mealkitShippingInfos) {
}
