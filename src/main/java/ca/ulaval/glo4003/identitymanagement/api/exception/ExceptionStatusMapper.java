package ca.ulaval.glo4003.identitymanagement.api.exception;

import java.util.WeakHashMap;

import ca.ulaval.glo4003.identitymanagement.domain.exception.IdentityManagementException;
import ca.ulaval.glo4003.identitymanagement.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.identitymanagement.domain.exception.InvalidPasswordException;
import ca.ulaval.glo4003.identitymanagement.domain.exception.InvalidTokenException;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserNotFoundException;

import jakarta.ws.rs.core.Response;

public class ExceptionStatusMapper {
    private static WeakHashMap<Class<? extends IdentityManagementException>, Response.Status> exceptionMapper;

    public static Response.Status getResponseStatus(IdentityManagementException exception) {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper.get(exception.getClass());
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(UserNotFoundException.class, Response.Status.NOT_FOUND);
        exceptionMapper.put(UserAlreadyExistsException.class, Response.Status.BAD_REQUEST);

        exceptionMapper.put(InvalidTokenException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidPasswordException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidCredentialsException.class, Response.Status.UNAUTHORIZED);
    }
}
