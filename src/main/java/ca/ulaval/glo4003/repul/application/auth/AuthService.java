package ca.ulaval.glo4003.repul.application.auth;

import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ca.ulaval.glo4003.repul.application.auth.parameter.LoginParams;
import ca.ulaval.glo4003.repul.application.auth.parameter.RegisterParams;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.Email;
import ca.ulaval.glo4003.repul.domain.exception.UserAlreadyExistsException;
import ca.ulaval.glo4003.repul.domain.exception.UserNotFoundException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class AuthService {
    private final UserRepository userRepository;
    private final RepULRepository repULRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, RepULRepository repULRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.repULRepository = repULRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String login(LoginParams loginParams) {
        String hashPassword = this.userRepository.getUserPassword(loginParams.email());
        if (hashPassword == null) {
            throw new UserNotFoundException();
        }
        if (bCryptPasswordEncoder.matches(loginParams.password(), hashPassword)) {
            return createToken(loginParams.email());
        }
        throw new UserNotFoundException();
    }

    public String register(RegisterParams registerParams) {
        if (this.userRepository.doesUserExist(registerParams.email())) {
            throw new UserAlreadyExistsException();
        }
        String hashPassword = this.bCryptPasswordEncoder.encode(registerParams.password());
        userRepository.addUser(registerParams.email(),
            hashPassword);
        // TODO add the good parameters
        RepUL repUL = this.repULRepository.get();
        repUL.register();
        this.repULRepository.saveOrUpdate(repUL);
        return createToken(registerParams.email());
    }

    private String createToken(Email email) {
        Algorithm algorithm = Algorithm.HMAC256("a");
        long sixtyMinutesFromNow = System.currentTimeMillis() + 3600 * 1000;
        Date expireTime = new Date(sixtyMinutesFromNow);
        return JWT.create()
            .withClaim("email", email.email())
            .withExpiresAt(expireTime)
            .sign(algorithm);
    }
}
