package com.veterinaria.demo.ErrorHeading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)  // Indica que es un error 404
public class NotFoundException extends RuntimeException {
    private int id;

    public NotFoundException(int id, String message) {
        super(message);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
