package com.dhmoney.accountservice.utils;

import com.dhmoney.accountservice.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 401) {
            return new UnauthorizedException("Unauthorized");
        }

        JsonResponseError message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, JsonResponseError.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (response.status()) {
            case 400:
                return new ResourceBadRequestException(message.getError() != null ? message.getError() : "Bad Request");
            case 401:
                return new UnauthorizedException(message.getError() != null ? message.getError() : "Unauthorized");
            case 403:
                return new ForbiddenException(message.getError() != null ? message.getError() : "Forbidden");
            case 404:
                return new ResourceNotFoundException(message.getError() != null ? message.getError() : "Not found");
            case 409:
                return new ResourceConflictException(message.getError() != null ? message.getError() : "Conflict");
            case 500:
                return new Exception(message.getError() != null ? message.getError() : "Internal Server Error");
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}