package com.netology.diplom.exceptions;

public class InvalidJwtException  extends RuntimeException{
    public InvalidJwtException(String message) {
        super(message);
    }
}
