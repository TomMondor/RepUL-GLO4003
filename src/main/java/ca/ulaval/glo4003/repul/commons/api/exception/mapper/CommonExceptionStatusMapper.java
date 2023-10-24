package ca.ulaval.glo4003.repul.commons.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.commons.domain.exception.CommonException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidAmountException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidEmailException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidMealKitTypeException;

import jakarta.ws.rs.core.Response;

public class CommonExceptionStatusMapper {
    private static WeakHashMap<Class<? extends CommonException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends CommonException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(InvalidEmailException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidAmountException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLocationException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLocationIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidMealKitTypeException.class, Response.Status.BAD_REQUEST);
    }
}
