package ma.adria.transactionservice.service;
import org.springframework.stereotype.Service;import org.springframework.beans.factory.annotation.Value;import ma.adria.transactionservice.domain.BankTransaction;import ma.adria.transactionservice.repository.TransactionRepository;import java.util.*;import java.math.*;import java.time.Instant;
@Service public class TransactionService {
private final TransactionRepository r;private final TransactionStore store;private final AccountClient accounts;private final BigDecimal max;
public TransactionService(TransactionRepository r,TransactionStore store,AccountClient accounts,@Value("${bank.transaction.max-amount:1000}") BigDecimal max){this.r=r;this.store=store;this.accounts=accounts;this.max=max;}
public record Request(UUID id,UUID accountId,UUID destinationAccountId,BigDecimal amount,String currency,String merchantCategory,String country){}
public BankTransaction create(Request q){Objects.requireNonNull(q.id());Objects.requireNonNull(q.accountId());if(q.amount()==null||q.amount().signum()<=0||q.amount().compareTo(max)>0)throw new IllegalArgumentException("Montant hors plafond");q.amount().setScale(2,RoundingMode.UNNECESSARY);Currency.getInstance(q.currency());if(q.country()==null||q.merchantCategory()==null)throw new IllegalArgumentException("Pays et catégorie requis");
BankTransaction t=new BankTransaction();t.id=q.id();t.accountId=q.accountId();t.destinationAccountId=q.destinationAccountId();t.amount=q.amount();t.currency=q.currency();t.country=q.country();t.merchantCategory=q.merchantCategory();t.createdAt=Instant.now();t.status="PENDING";
t=store.savePending(t);if("COMPLETED".equals(t.status))return t;
// Consultation obligatoire. La validation atomique finale reste dans account-service.
accounts.get(t.accountId);
accounts.apply(new AccountClient.Operation(t.id,t.accountId,t.destinationAccountId,t.amount,t.currency));
return store.complete(t.id);
}
public BankTransaction get(UUID id){return r.findById(id).orElseThrow(()->new IllegalArgumentException("Transaction absente"));}
public List<BankTransaction> list(){return r.findAll(org.springframework.data.domain.PageRequest.of(0,100)).getContent();}
}
