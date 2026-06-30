package dev.sorokin.eventmanager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorMessageResponse> handleApiException(ApiException e) {
        log.error("Api error: {}", e.getStatus().getReasonPhrase());

        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                e.getStatus().getReasonPhrase(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(e.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleMessageNotReadable(HttpMessageNotReadableException e) {
        String message = "Request body is required";

        if (e.getMessage() != null && e.getMessage().contains("Required request body is missing")) {
            message = "Request body is missing";
        } else if (e.getMessage() != null && e.getMessage().contains("JSON parse error")) {
            message = "Invalid JSON format";
        }

        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "JSON body error",
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.error(errorMessage);

        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "Validation error",
                errorMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String requiredType = e.getRequiredType().getSimpleName();

        log.error("Incorrect params");

        String errorMessage = String.format(
                "Param '%s' should be type %s'",
                paramName, requiredType
        );

        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "Type mismatch",
                errorMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationException(AuthorizationDeniedException e) {
        log.error("Handle authorization exception", e);

        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "Forbidden",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException(Exception e) {
        log.error("Unhandled exception: ", e);

        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "Internal server error",
                "An unexpected error occurred",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
