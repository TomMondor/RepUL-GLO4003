package ca.ulaval.glo4003.repul.identitymanagement.domain.token;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Role;

public interface TokenGenerator {
    Token generate(UniqueIdentifier uid, Role role);
}
