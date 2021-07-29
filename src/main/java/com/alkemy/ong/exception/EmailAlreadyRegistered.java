package com.alkemy.ong.exception;

public class EmailAlreadyRegistered extends RuntimeException {
    public EmailAlreadyRegistered(String message){
        super(message);
    }
}
