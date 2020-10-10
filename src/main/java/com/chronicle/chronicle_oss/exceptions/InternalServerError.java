package com.chronicle.chronicle_oss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Server Error")
public class InternalServerError extends RuntimeException {
}
