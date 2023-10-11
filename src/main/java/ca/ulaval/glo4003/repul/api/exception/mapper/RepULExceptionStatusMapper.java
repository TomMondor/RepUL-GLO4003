package ca.ulaval.glo4003.repul.api.exception.mapper;

import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.domain.exception.InvalidBirthdateException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidFrequencyException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidGenderException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLunchboxException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLunchboxTypeException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidOrderIdException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidQuantityException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidRecipeException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidSemesterCodeException;
import ca.ulaval.glo4003.repul.domain.exception.InvalidSubscriptionIdException;
import ca.ulaval.glo4003.repul.domain.exception.OrderNotPendingException;
import ca.ulaval.glo4003.repul.domain.exception.RepULException;
import ca.ulaval.glo4003.repul.domain.exception.RepULNotFoundException;

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

        exceptionMapper.put(RepULNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(InvalidFrequencyException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLocationException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLocationIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLunchboxException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidOrderIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidQuantityException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidRecipeException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidSemesterCodeException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidSubscriptionIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidGenderException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLunchboxTypeException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(OrderNotPendingException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidBirthdateException.class, Response.Status.BAD_REQUEST);
    }
}
