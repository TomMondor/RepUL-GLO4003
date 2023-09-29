package ca.ulaval.glo4003.identitymanagement.domain.token;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Role;

public record DecodedToken(UniqueIdentifier userId, Role role) {
}
