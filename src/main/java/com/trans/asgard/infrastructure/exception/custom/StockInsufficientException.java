package com.trans.asgard.infrastructure.exception.custom;

public class StockInsufficientException extends RuntimeException{
    public StockInsufficientException(String message){
        super(message);
    }
}
