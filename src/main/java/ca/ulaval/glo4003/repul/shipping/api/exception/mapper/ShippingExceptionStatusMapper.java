package ca.ulaval.glo4003.repul.shipping.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.shipping.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.shipping.application.exception.ShippingNotFoundException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidLockerIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShipperException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShippingIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.MealKitNotInDeliveryException;
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

        exceptionMapper.put(DeliveryPersonNotFoundException.class, Response.Status.NOT_FOUND);
        exceptionMapper.put(ShippingNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(InvalidLockerIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidShipperException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidShippingIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotDeliveredException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotInDeliveryException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidMealKitIdException.class, Response.Status.BAD_REQUEST);
    }
}
