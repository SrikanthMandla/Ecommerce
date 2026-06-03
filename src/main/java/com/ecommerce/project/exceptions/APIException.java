package com.ecommerce.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public APIException(){

    }
   public APIException(String message){
        super(message);
   }


}
