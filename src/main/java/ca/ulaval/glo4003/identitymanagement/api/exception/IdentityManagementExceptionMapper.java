package ca.ulaval.glo4003.identitymanagement.api.exception;

import ca.ulaval.glo4003.commons.api.exception.response.ErrorResponse;
import ca.ulaval.glo4003.identitymanagement.domain.exception.IdentityManagementException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IdentityManagementExceptionMapper implements ExceptionMapper<IdentityManagementException> {
    @Override
    public Response toResponse(IdentityManagementException exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage());
        return Response.status(ExceptionStatusMapper.getResponseStatus(exception))
            .entity(response).build();
    }
}
