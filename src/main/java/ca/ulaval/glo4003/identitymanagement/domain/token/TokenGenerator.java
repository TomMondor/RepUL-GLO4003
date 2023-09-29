package ca.ulaval.glo4003.identitymanagement.domain.token;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Role;

public interface TokenGenerator {
    Token generate(UniqueIdentifier uid, Role role);
}
