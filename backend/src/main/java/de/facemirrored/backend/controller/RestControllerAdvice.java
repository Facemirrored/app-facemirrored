package de.facemirrored.backend.controller;

import de.facemirrored.backend.exceptions.SignUpUserFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationException(
      final AuthenticationException authenticationException,
      final WebRequest webRequest) {

    return handleExceptionInternal(
        authenticationException,
        null,
        new HttpHeaders(),
        HttpStatus.UNAUTHORIZED,
        webRequest);
  }

  @ExceptionHandler(value = SignUpUserFailedException.class)
  protected ResponseEntity<Object> handleSignUpUserFailedException(
      final SignUpUserFailedException signUpUserFailedException,
      final WebRequest webRequest) {

    return handleExceptionInternal(
        signUpUserFailedException,
        signUpUserFailedException.getUsername(),
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        webRequest);
  }
}
