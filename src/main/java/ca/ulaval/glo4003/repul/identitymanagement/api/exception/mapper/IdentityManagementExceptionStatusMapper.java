package ca.ulaval.glo4003.repul.identitymanagement.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.IdentityManagementException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.InvalidPasswordException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.InvalidTokenException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.PasswordNotMatchingException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.TokenVerificationFailedException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.UserNotFoundException;
import ca.ulaval.glo4003.repul.identitymanagement.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.ws.rs.core.Response;

public class IdentityManagementExceptionStatusMapper {
    private static WeakHashMap<Class<? extends IdentityManagementException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends IdentityManagementException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(UserNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(InvalidTokenException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(IDULAlreadyInUseException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(EmailAlreadyInUseException.class, Response.Status.BAD_REQUEST);

        exceptionMapper.put(InvalidCredentialsException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(InvalidPasswordException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(PasswordNotMatchingException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(TokenVerificationFailedException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(MissingAuthorizationHeaderException.class, Response.Status.UNAUTHORIZED);
    }
}
