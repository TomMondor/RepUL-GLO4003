package ca.ulaval.glo4003.commons.api.exception.mapper;

import ca.ulaval.glo4003.commons.api.exception.response.ErrorResponse;
import ca.ulaval.glo4003.commons.domain.exception.CommonException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CommonExceptionMapper implements ExceptionMapper<CommonException> {

    @Override
    public Response toResponse(CommonException exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage());
        return Response.status(CommonExceptionStatusMapper.getResponseStatus(exception)).entity(response).build();
    }
}
