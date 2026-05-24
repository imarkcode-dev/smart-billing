package com.smart.billing.app.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.smart.billing.app.dto.ErrorResponse;

/**
 * Global exception handler for the Smart Billing application.
 * 
 * This class is responsible for handling exceptions thrown by controllers
 * across the entire application. It uses Spring's @ControllerAdvice annotation
 * to intercept exceptions and provide consistent error responses.
 * 
 * The handler provides specific treatment for:
 * - ResourceNotFoundException: Returns HTTP 404 with error details
 * - Generic Exception: Returns HTTP 400 with error details
 * 
 * All error responses are formatted using the ErrorResponse DTO containing
 * timestamp, status code, and error message.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns HTTP 404.
     */
   @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                ));
    }

    /**
     * Handles generic exceptions and returns HTTP 400.
     */
     @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }

    /**
     * Handles BadCredentialsException and returns HTTP 401.
     * This exception is thrown when invalid credentials (wrong password) are provided.
     *
     * @param ex the BadCredentialsException thrown
     * @return ResponseEntity with HTTP 401 Unauthorized status and error message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    /**
     * Handles RuntimeException and returns HTTP 500.
     * This is a catch-all handler for unexpected runtime exceptions during execution.
     *
     * @param ex the RuntimeException thrown
     * @return ResponseEntity with HTTP 500 Internal Server Error status and error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /**
     * Handles HttpMessageNotReadableException and returns HTTP 400.
     * This exception is thrown when the request body contains malformed or invalid JSON.
     *
     * @param ex the HttpMessageNotReadableException thrown
     * @return ResponseEntity with HTTP 400 Bad Request status and error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Malformed JSON request");
    }

    /**
     * Handles MethodArgumentTypeMismatchException and returns HTTP 400.
     *
     * This exception is thrown when a request parameter or path variable
     * cannot be converted to the expected type. For example, if the endpoint
     * expects an Integer ID but receives a non-numeric string such as "abc",
     * Spring will raise this exception.
     *
     * @param ex the MethodArgumentTypeMismatchException thrown by Spring
     *           when the argument type does not match the expected type
     * @return ResponseEntity with HTTP 400 (Bad Request) status and a message
     *         indicating the invalid parameter type along with the value received
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid parameter type: " + ex.getValue());
    }

    /**
     * Handles IllegalArgumentException thrown within the application.
     * 
     * This method intercepts any IllegalArgumentException raised by controllers
     * and returns a standardized HTTP 400 (Bad Request) response. The response body
     * contains a JSON object with a single key "message" that provides details
     * about the error.
     *
     * Example response:
     * {
     *   "message": "Cliente no encontrado en el sistema"
     * }
     *
     * @param ex the IllegalArgumentException instance containing the error details
     * @return a ResponseEntity with HTTP status 400 and a JSON body describing the error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }


}
