package com.netology.diplom.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({MaxUploadSizeExceededException.class, })
    public ResponseEntity<ApiErrorResponse> catchMaxUploadSizeExceeded(MaxUploadSizeExceededException e){
        return new ResponseEntity<>(ApiErrorResponse.builder().message(e.getMessage()).build(),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ApiErrorResponse> catchInvalidJwtException(InvalidJwtException e){
        return new ResponseEntity<>(ApiErrorResponse.builder().message(e.getMessage()).build(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> serverException (Exception e){
        return new ResponseEntity<>(ApiErrorResponse.builder().message(e.getMessage()).build(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ApiErrorResponse> userExistsException (Exception e){
        return new ResponseEntity<>(ApiErrorResponse.builder().message(e.getMessage()).build(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> userNotFoundExceptionHandler (Exception e){
        return new ResponseEntity<>(ApiErrorResponse.builder().message(e.getMessage()).build(),HttpStatus.NOT_FOUND);
    }

}
