package com.finpulse.account_service.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String customerReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private double balance;

    @Version
    private Long version;

    protected Account() {
    }

    public Account(
            String customerReference,
            String currency,
            BigDecimal initialBalance
    ) {
        this.customerReference = customerReference;
        this.currency = currency;
        this.balance = initialBalance.doubleValue();
        this.status = AccountStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getCurrency() {
        return currency;
    }

    public double getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }

    public void changeStatus(AccountStatus status) {
        this.status = status;
    }
    public void debit(BigDecimal amount) {
        this.balance = BigDecimal.valueOf(this.balance)
                .subtract(amount)
                .doubleValue();
    }

    public void credit(BigDecimal amount) {
        this.balance = BigDecimal.valueOf(this.balance)
                .add(amount)
                .doubleValue();
    }
}
