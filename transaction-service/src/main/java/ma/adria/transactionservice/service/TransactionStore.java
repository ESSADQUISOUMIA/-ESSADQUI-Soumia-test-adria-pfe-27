package ma.adria.transactionservice.service;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import ma.adria.transactionservice.domain.BankTransaction;import ma.adria.transactionservice.repository.TransactionRepository;import java.util.*;
@Service public class TransactionStore {private final TransactionRepository r;public TransactionStore(TransactionRepository r){this.r=r;}
@Transactional public BankTransaction savePending(BankTransaction t){var old=r.findById(t.id);if(old.isPresent()){var x=old.get();if(!Objects.equals(x.accountId,t.accountId)||!Objects.equals(x.destinationAccountId,t.destinationAccountId)||x.amount.compareTo(t.amount)!=0||!Objects.equals(x.currency,t.currency)||!Objects.equals(x.country,t.country)||!Objects.equals(x.merchantCategory,t.merchantCategory))throw new IllegalArgumentException("Identifiant déjà utilisé");return x;}return r.saveAndFlush(t);}
@Transactional public BankTransaction complete(UUID id){var t=r.lock(id).orElseThrow();t.status="COMPLETED";return t;}
}
