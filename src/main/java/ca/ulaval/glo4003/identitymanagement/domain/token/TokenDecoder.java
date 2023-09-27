package ca.ulaval.glo4003.identitymanagement.domain.token;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;

public interface TokenDecoder {
    UniqueIdentifier decode(String token);
}
