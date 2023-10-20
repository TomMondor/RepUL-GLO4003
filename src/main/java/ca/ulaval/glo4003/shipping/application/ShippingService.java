package ca.ulaval.glo4003.shipping.application;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.application.query.MealkitShippingInfoQuery;
import ca.ulaval.glo4003.shipping.application.query.ShippingInfoQuery;
import ca.ulaval.glo4003.shipping.domain.Shipping;
import ca.ulaval.glo4003.shipping.domain.ShippingRepository;
import ca.ulaval.glo4003.shipping.domain.exception.ShippingNotFoundException;

public class ShippingService {
    private final ShippingRepository shippingRepository;

    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    public void receiveShippingRequest(ShippingInfoQuery shippingInfoQuery) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        LocationId shippingLocationId = new LocationId(shippingInfoQuery.pickupLocationId());

        List<MealkitShippingInfoQuery> mealkitShippingInfos = shippingInfoQuery.mealkitShippingInfos().stream()
            .map(mealkitInfo -> new MealkitShippingInfoQuery(mealkitInfo.shippingLocationId(), mealkitInfo.caseId(), mealkitInfo.mealkitId())).toList();
        shipping.createShippingTicket(shippingLocationId, mealkitShippingInfos);

        notifyShipper();

        shippingRepository.saveOrUpdate(shipping);
    }

    private void notifyShipper() {
        // TODO envoyer un message au shipper par un event au system de notification
    }

    public void pickupCargo(UniqueIdentifier accountId, UniqueIdentifier shippingId) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.pickupCargo(accountId, shippingId);

        shippingRepository.saveOrUpdate(shipping);
    }

    public void cancelShipping(UniqueIdentifier accountId, UniqueIdentifier from) {
        Shipping shipping = shippingRepository.get().orElseThrow(ShippingNotFoundException::new);

        shipping.cancelShipping(accountId, from);

        shippingRepository.saveOrUpdate(shipping);
    }
}
