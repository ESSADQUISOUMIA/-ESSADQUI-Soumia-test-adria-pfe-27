package ma.adria.transactionservice.web.graphql;
import org.springframework.stereotype.Controller;import org.springframework.graphql.data.method.annotation.*;import ma.adria.transactionservice.service.TransactionService;import ma.adria.transactionservice.domain.BankTransaction;import java.util.*;import java.math.BigDecimal;
@Controller public class TransactionGraphql {private final TransactionService s;public TransactionGraphql(TransactionService s){this.s=s;}
public record Input(UUID id,UUID accountId,UUID destinationAccountId,String amount,String currency,String merchantCategory,String country){}
@QueryMapping public List<BankTransaction> transactions(){return s.list();}@QueryMapping public BankTransaction transaction(@Argument UUID id){return s.get(id);}
@MutationMapping public BankTransaction createTransaction(@Argument Input input){return s.create(new TransactionService.Request(input.id(),input.accountId(),input.destinationAccountId(),new BigDecimal(input.amount()),input.currency(),input.merchantCategory(),input.country()));}
}
