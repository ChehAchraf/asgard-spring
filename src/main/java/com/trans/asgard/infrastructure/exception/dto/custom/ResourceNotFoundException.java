package com.trans.asgard.infrastructure.exception.dto.custom;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){
        super(message);
    }

}
