package com.finpulse.account_service.web.graphql;


import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import jakarta.validation.ConstraintViolationException;
import com.finpulse.micros_adria.accountservice.domain.AccountStatus;
import com.finpulse.micros_adria.accountservice.service.AccountException;
import com.finpulse.micros_adria.accountservice.service.AccountService;
import com.finpulse.micros_adria.accountservice.service.dto.AccountResponse;
import com.finpulse.micros_adria.accountservice.service.dto.CreateAccountRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
public class AccountGraphqlController {

    private final AccountService service;

    public AccountGraphqlController(AccountService service) {
        this.service = service;
    }

    @QueryMapping
    public AccountResponse account(@Argument UUID id) {
        return service.getById(id);
    }

    @QueryMapping
    public Page<AccountResponse> accounts(
            @Argument int page,
            @Argument int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page doit être positif ou nul ; size doit être entre 1 et 100."
            );
        }

        return service.findAll(PageRequest.of(page, size));
    }

    @MutationMapping
    public AccountResponse createAccount(
            @Argument CreateAccountInput input
    ) {
        return service.create(new CreateAccountRequest(
                input.customerReference(),
                input.currency(),
                new BigDecimal(input.initialBalance())
        ));
    }

    @MutationMapping
    public AccountResponse changeAccountStatus(
            @Argument UUID id,
            @Argument AccountStatus status
    ) {
        return service.changeStatus(id, status);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBusiness(
            AccountException exception,
            DataFetchingEnvironment environment
    ) {
        ErrorType type =
                exception.getCode() == AccountException.Code.NOT_FOUND
                        ? ErrorType.NOT_FOUND
                        : ErrorType.BAD_REQUEST;

        return GraphqlErrorBuilder.newError(environment)
                .errorType(type)
                .message(exception.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidArgument(
            IllegalArgumentException exception,
            DataFetchingEnvironment environment
    ) {
        return GraphqlErrorBuilder.newError(environment)
                .errorType(ErrorType.BAD_REQUEST)
                .message("Paramètres invalides : " + exception.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleValidation(
            ConstraintViolationException exception,
            DataFetchingEnvironment environment
    ) {
        return GraphqlErrorBuilder.newError(environment)
                .errorType(ErrorType.BAD_REQUEST)
                .message(exception.getMessage())
                .build();
    }

    public record CreateAccountInput(
            String customerReference,
            String currency,
            String initialBalance
    ) {
    }
}
