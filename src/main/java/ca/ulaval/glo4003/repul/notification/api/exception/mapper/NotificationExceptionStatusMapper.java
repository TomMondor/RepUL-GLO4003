package ca.ulaval.glo4003.repul.notification.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.notification.application.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.application.exception.NotificationException;

import jakarta.ws.rs.core.Response;

public class NotificationExceptionStatusMapper {
    private static WeakHashMap<Class<? extends NotificationException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends NotificationException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(AccountNotFoundException.class, Response.Status.NOT_FOUND);
    }
}
