package com.chronicle.chronicle_oss.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;

@NoArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "request isn't correct")
public class BadRequestException extends RuntimeException {

    public BadRequestException(String key) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown key: " + key);
    }

    public BadRequestException(String key, Class expectedClass) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type for value with key " + key + " should be: " + expectedClass);
    }

    public BadRequestException(Collection<String> keys) {
        throw new BadRequestException(keys.toArray(new String[0]));
    }

    public BadRequestException(String... keys) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown keys: " + Arrays.toString(keys));
    }
}
