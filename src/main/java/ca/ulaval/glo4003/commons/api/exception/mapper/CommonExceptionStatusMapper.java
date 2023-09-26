package ca.ulaval.glo4003.commons.api.exception.mapper;

import java.util.WeakHashMap;

import ca.ulaval.glo4003.commons.domain.exception.CommonException;
import ca.ulaval.glo4003.commons.domain.exception.InvalidEmailException;

import jakarta.ws.rs.core.Response;

public class CommonExceptionStatusMapper {
    private static WeakHashMap<Class<? extends CommonException>, Response.Status> exceptionMapper;

    public static Response.Status getResponseStatus(CommonException commonException) {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper.get(commonException.getClass());
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(InvalidEmailException.class, Response.Status.BAD_REQUEST);
    }
}
