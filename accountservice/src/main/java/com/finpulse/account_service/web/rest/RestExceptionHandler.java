package com.finpulse.account_service.web.rest;

import jakarta.validation.ConstraintViolationException;
import com.finpulse.micros_adria.accountservice.service.AccountException;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ProblemDetail handleBusiness(AccountException exception) {
        HttpStatus status =
                exception.getCode() == AccountException.Code.NOT_FOUND
                        ? HttpStatus.NOT_FOUND
                        : HttpStatus.BAD_REQUEST;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                exception.getMessage()
        );

        problem.setProperty("code", exception.getCode().name());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleRequestValidation(
            MethodArgumentNotValidException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Les données transmises sont invalides."
        );

        problem.setProperty(
                "errors",
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField() + " : "
                                        + error.getDefaultMessage())
                        .toList()
        );

        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleServiceValidation(
            ConstraintViolationException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleConflict(
            OptimisticLockingFailureException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Le compte a été modifié simultanément. Rechargez les données."
        );
    }
}