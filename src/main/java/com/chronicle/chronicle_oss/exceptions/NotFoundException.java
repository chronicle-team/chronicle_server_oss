package com.chronicle.chronicle_oss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "content not found")
public class NotFoundException extends RuntimeException {
    public NotFoundException(String key) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "content not found" + key);
    }
}
