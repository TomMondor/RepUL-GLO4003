package ca.ulaval.glo4003.repul.shipping.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.shipping.application.exception.DeliveryAccountNotFoundException;
import ca.ulaval.glo4003.repul.shipping.application.exception.ShippingNotFoundException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShipperException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShippingIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.ShippingException;

import jakarta.ws.rs.core.Response;

public class ShippingExceptionStatusMapper {
    private static WeakHashMap<Class<? extends ShippingException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends ShippingException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(DeliveryAccountNotFoundException.class, Response.Status.NOT_FOUND);
        exceptionMapper.put(ShippingNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(InvalidShipperException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidShippingIdException.class, Response.Status.BAD_REQUEST);
    }
}
