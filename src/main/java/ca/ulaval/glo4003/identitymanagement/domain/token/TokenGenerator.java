package ca.ulaval.glo4003.identitymanagement.domain.token;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;

public interface TokenGenerator {
    Token generate(UniqueIdentifier uid);
}
