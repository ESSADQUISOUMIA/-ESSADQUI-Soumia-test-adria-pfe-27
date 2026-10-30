package com.finpulse.account_service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.finpulse.micros_adria.accountservice.domain.Account;
import com.finpulse.micros_adria.accountservice.domain.AccountOperation;
import com.finpulse.micros_adria.accountservice.domain.AccountStatus;
import com.finpulse.micros_adria.accountservice.repository.AccountOperationRepository;
import com.finpulse.micros_adria.accountservice.repository.AccountRepository;
import com.finpulse.micros_adria.accountservice.service.dto.ApplyOperationRequest;
import com.finpulse.micros_adria.accountservice.service.dto.OperationResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@Validated
public class AccountOperationService {

    /*
     * Limite volontaire pour conserver une précision monétaire raisonnable
     * avec le double imposé par le sujet.
     */
    private static final BigDecimal MAX_BALANCE =
            new BigDecimal("999999999999.99");

    private final AccountRepository accounts;
    private final AccountOperationRepository operations;
    private final BigDecimal maxAmount;

    public AccountOperationService(
            AccountRepository accounts,
            AccountOperationRepository operations,
            @Value("${bank.transaction.max-amount:1000}")
            BigDecimal maxAmount
    ) {
        this.accounts = accounts;
        this.operations = operations;
        this.maxAmount = maxAmount;
    }

    @Transactional
    public OperationResponse apply(
            @NotNull @Valid ApplyOperationRequest request
    ) {
        UUID sourceId = request.sourceAccountId();
        UUID destinationId = request.destinationAccountId();

        if (sourceId.equals(destinationId)) {
            throw error(
                    AccountException.Code.SAME_ACCOUNT,
                    "Les comptes source et destinataire doivent être différents."
            );
        }

        Account source;
        Account destination = null;

        if (destinationId == null) {
            source = lockAccount(sourceId);
        } else if (sourceId.compareTo(destinationId) < 0) {
            source = lockAccount(sourceId);
            destination = lockAccount(destinationId);
        } else {
            destination = lockAccount(destinationId);
            source = lockAccount(sourceId);
        }

        /*
         * Vérification après acquisition des verrous :
         * une nouvelle tentative attend la fin du premier traitement.
         */
        AccountOperation existing = operations
                .findById(request.operationId())
                .orElse(null);

        if (existing != null) {
            verifySameRequest(existing, request);
            return OperationResponse.from(existing);
        }

        if (request.amount().compareTo(maxAmount) > 0) {
            throw error(
                    AccountException.Code.LIMIT_EXCEEDED,
                    "Le montant dépasse le plafond de " + maxAmount + "."
            );
        }

        verifyAccount(source, request.currency());

        if (destination != null) {
            verifyAccount(destination, request.currency());

            BigDecimal resultingBalance =
                    BigDecimal.valueOf(destination.getBalance())
                            .add(request.amount());

            if (resultingBalance.compareTo(MAX_BALANCE) > 0) {
                throw error(
                        AccountException.Code.BALANCE_LIMIT_EXCEEDED,
                        "Le solde du destinataire dépasserait la limite autorisée."
                );
            }
        }

        BigDecimal availableBalance =
                BigDecimal.valueOf(source.getBalance());

        if (availableBalance.compareTo(request.amount()) < 0) {
            throw error(
                    AccountException.Code.INSUFFICIENT_BALANCE,
                    "Le solde du compte source est insuffisant."
            );
        }

        source.debit(request.amount());

        if (destination != null) {
            destination.credit(request.amount());
        }

        AccountOperation operation = new AccountOperation(
                request.operationId(),
                sourceId,
                destinationId,
                request.amount(),
                request.currency()
        );

        /*
         * Les soldes et l'opération sont enregistrés dans la même
         * transaction locale. Toute erreur provoque leur rollback.
         */
        operations.saveAndFlush(operation);

        return OperationResponse.from(operation);
    }

    @Transactional(readOnly = true)
    public OperationResponse getById(@NotNull UUID operationId) {
        return operations.findById(operationId)
                .map(OperationResponse::from)
                .orElseThrow(() -> error(
                        AccountException.Code.NOT_FOUND,
                        "Opération introuvable : " + operationId
                ));
    }

    private Account lockAccount(UUID id) {
        return accounts.findByIdForUpdate(id)
                .orElseThrow(() -> error(
                        AccountException.Code.NOT_FOUND,
                        "Compte introuvable : " + id
                ));
    }

    private void verifyAccount(Account account, String currency) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw error(
                    AccountException.Code.ACCOUNT_BLOCKED,
                    "Le compte " + account.getId() + " est bloqué."
            );
        }

        if (!account.getCurrency().equals(currency)) {
            throw error(
                    AccountException.Code.CURRENCY_MISMATCH,
                    "La devise ne correspond pas à celle du compte."
            );
        }
    }

    private void verifySameRequest(
            AccountOperation existing,
            ApplyOperationRequest request
    ) {
        boolean same =
                existing.getSourceAccountId()
                        .equals(request.sourceAccountId())
                        && Objects.equals(
                        existing.getDestinationAccountId(),
                        request.destinationAccountId()
                )
                        && existing.getAmount().compareTo(request.amount()) == 0
                        && existing.getCurrency().equals(request.currency());

        if (!same) {
            throw error(
                    AccountException.Code.IDEMPOTENCY_CONFLICT,
                    "Cet identifiant d'opération a déjà été utilisé "
                            + "avec d'autres paramètres."
            );
        }
    }

    private AccountException error(
            AccountException.Code code,
            String message
    ) {
        return new AccountException(code, message);
    }
}