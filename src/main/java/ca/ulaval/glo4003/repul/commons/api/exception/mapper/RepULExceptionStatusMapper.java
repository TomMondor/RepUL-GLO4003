package ca.ulaval.glo4003.repul.commons.api.exception.mapper;

import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;
import ca.ulaval.glo4003.repul.cooking.api.exception.mapper.CookingExceptionStatusMapper;
import ca.ulaval.glo4003.repul.notification.api.exception.mapper.NotificationExceptionStatusMapper;
import ca.ulaval.glo4003.repul.shipping.api.exception.mapper.ShippingExceptionStatusMapper;
import ca.ulaval.glo4003.repul.subscription.api.exception.mapper.SubscriptionExceptionStatusMapper;
import ca.ulaval.glo4003.repul.user.api.exception.mapper.UserExceptionStatusMapper;

import jakarta.ws.rs.core.Response;

public class RepULExceptionStatusMapper {
    private static WeakHashMap<Class<? extends RepULException>, Response.Status> exceptionMapper;

    public static Response.Status getResponseStatus(RepULException repULException) {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper.get(repULException.getClass());
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();
        exceptionMapper.putAll(UserExceptionStatusMapper.getAll());
        exceptionMapper.putAll(SubscriptionExceptionStatusMapper.getAll());
        exceptionMapper.putAll(ShippingExceptionStatusMapper.getAll());
        exceptionMapper.putAll(CommonExceptionStatusMapper.getAll());
        exceptionMapper.putAll(CookingExceptionStatusMapper.getAll());
        exceptionMapper.putAll(NotificationExceptionStatusMapper.getAll());
    }
}
