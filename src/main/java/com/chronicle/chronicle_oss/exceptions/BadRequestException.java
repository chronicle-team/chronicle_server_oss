package com.chronicle.chronicle_oss.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@NoArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "request isn't correct")
public class BadRequestException extends RuntimeException {

    public BadRequestException(String key) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown key: " + key);
    }
}
