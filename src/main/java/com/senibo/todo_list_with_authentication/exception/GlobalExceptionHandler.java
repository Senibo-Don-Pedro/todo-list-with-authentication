package com.senibo.todo_list_with_authentication.exception;

import com.senibo.todo_list_with_authentication.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // **THIS IS THE KEY HANDLER FOR YOUR 403 ISSUE**
    // Handles malformed JSON requests (like your missing closing brace)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidJson(HttpMessageNotReadableException ex) {
        log.debug("Invalid JSON received: {}", ex.getMessage());

        String errorMessage = "Invalid JSON format";

        //        // Extract more specific error details if available
        //        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
        //            String causeMessage = ex.getCause().getMessage();
        //            if (causeMessage.contains("Unexpected end-of-input")) {
        //                errorMessage = "Invalid JSON: Missing closing brace or incomplete structure";
        //            } else if (causeMessage.contains("Unexpected character")) {
        //                errorMessage = "Invalid JSON: Unexpected character in request body";
        //            } else if (causeMessage.contains("Unrecognized token")) {
        //                errorMessage = "Invalid JSON: Unrecognized token in request body";
        //            }
        //        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiResponse.error(ex.getMessage()));
    }

    // DTO @Valid errors (e.g., empty JSON on /signup)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleDtoValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()))
                                .toList();
        log.debug("Validation failed: {}", errors);
        // If your ApiResponse supports a list:
        return ResponseEntity.badRequest()
                             .body(ApiResponse.error("Validation failed for the " + "following reasons:",
                                                     errors));
    }

    // Handle Spring Security authentication failures
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        log.debug("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ApiResponse.error("Invalid credentials"));
    }

    // Your service throws these for “username taken”, “email in use”, etc.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArg(IllegalArgumentException ex) {
        log.debug("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiResponse.error(ex.getMessage()));
    }

    // Handles access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArg(AccessDeniedException ex) {
        log.debug("Access Denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
    }


    // Catch-all for any unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(String.format(
                                     "An unexpected error occurred, %s",
                                     ex.getMessage())));
    }


}
