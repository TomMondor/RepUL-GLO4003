package ca.ulaval.glo4003.identitymanagement.application;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;

public interface AuthFacade {
    UniqueIdentifier register(String email, String password);
}
