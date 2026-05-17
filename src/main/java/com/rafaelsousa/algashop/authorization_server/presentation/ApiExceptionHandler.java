package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.user.management.AuthUserEmailAlreadyInUseException;
import com.rafaelsousa.algashop.authorization_server.domain.model.DomainException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String TIMESTAMP_PROPERTY_NAME = "timestamp";

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("One or more fields are invalid");
        problemDetail.setType(URI.create("/errors/invalid-fields"));

        Map<String, String> fieldErrors = ex.getBindingResult().getAllErrors().stream().collect(
                Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        objectError -> messageSource.getMessage(objectError, LocaleContextHolder.getLocale())
                )
        );

        problemDetail.setProperty("fields", fieldErrors);
        problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

	@ExceptionHandler({DomainException.class})
	public ProblemDetail handleUnprocessableEntityException(Exception ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
		problemDetail.setTitle("Unprocessable entity");
		problemDetail.setType(URI.create("/errors/unprocessable-entity"));
		problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

		return problemDetail;
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
		problemDetail.setType(URI.create("/errors/forbidden"));
		problemDetail.setTitle("Forbidden");
		problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

		return problemDetail;
	}

	@ExceptionHandler(AuthUserEmailAlreadyInUseException.class)
	public ProblemDetail handleAuthUserEmailAlreadyInUseException(AuthUserEmailAlreadyInUseException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		problemDetail.setType(URI.create("/errors/conflic"));
		problemDetail.setTitle("Conflict");
		problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

		return problemDetail;
	}

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal server error occurred. Please try again and if the problem persists, contact the system administrator.");
        problemDetail.setTitle("Internal server error");
        problemDetail.setType(URI.create("/errors/internal"));
        problemDetail.setProperty(TIMESTAMP_PROPERTY_NAME, OffsetDateTime.now());

        log.error(ex.getMessage(), ex);

        return problemDetail;
    }
}