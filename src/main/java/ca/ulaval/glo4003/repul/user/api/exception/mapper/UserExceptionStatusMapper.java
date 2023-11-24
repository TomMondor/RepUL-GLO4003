package ca.ulaval.glo4003.repul.user.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.user.application.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.user.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.application.exception.InvalidDateException;
import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidBirthdateException;
import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidGenderException;
import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidIDULException;
import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidNameException;
import ca.ulaval.glo4003.repul.user.domain.exception.UserException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidPasswordException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidTokenException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.PasswordNotMatchingException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.TokenVerificationFailedException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.UserNotFoundException;
import ca.ulaval.glo4003.repul.user.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.ws.rs.core.Response;

public class UserExceptionStatusMapper {
    private static WeakHashMap<Class<? extends UserException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends UserException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(AccountNotFoundException.class, Response.Status.NOT_FOUND);
        exceptionMapper.put(UserNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(InvalidBirthdateException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidDateException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidGenderException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidIDULException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidNameException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidTokenException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(IDULAlreadyInUseException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(EmailAlreadyInUseException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(CardNumberAlreadyInUseException.class, Response.Status.BAD_REQUEST);

        exceptionMapper.put(InvalidCredentialsException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(InvalidPasswordException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(PasswordNotMatchingException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(TokenVerificationFailedException.class, Response.Status.UNAUTHORIZED);
        exceptionMapper.put(MissingAuthorizationHeaderException.class, Response.Status.UNAUTHORIZED);
    }
}
