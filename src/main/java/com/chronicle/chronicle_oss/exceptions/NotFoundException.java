package com.chronicle.chronicle_oss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "content not found")
public class NotFoundException extends RuntimeException {

}
