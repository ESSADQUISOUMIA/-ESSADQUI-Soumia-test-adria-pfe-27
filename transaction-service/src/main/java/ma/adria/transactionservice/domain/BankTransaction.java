package ma.adria.transactionservice.domain;
import jakarta.persistence.*;import java.util.UUID;import java.time.Instant;import java.math.BigDecimal;
@Entity public class BankTransaction {@Id public UUID id;public UUID accountId;public UUID destinationAccountId;@Column(precision=19,scale=2) public BigDecimal amount;public String currency;public String merchantCategory;public String country;public Instant createdAt;public String status;public boolean published;public BankTransaction(){} }
