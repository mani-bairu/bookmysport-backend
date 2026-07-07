package com.bookmysport.backend.exception;

public class ResourseNotFoundException extends RuntimeException{

    public ResourseNotFoundException(String message){
        super(message);
    }
}
