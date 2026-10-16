package com.veterinaria.demo.ErrorHeading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)  // Código 401 para credenciales incorrectas
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException( String message) {
        super(message);
    }
}
