package ma.adria.transactionservice.service;
import org.springframework.cloud.openfeign.FeignClient;import org.springframework.web.bind.annotation.*;import java.util.UUID;import java.math.BigDecimal;
@FeignClient(name="account-service",url="${bank.account-url}") public interface AccountClient {
record Account(UUID id,String status,String currency,double balance){}
record Operation(UUID id,UUID source,UUID destination,BigDecimal amount,String currency){}
@GetMapping("/api/accounts/{id}") Account get(@PathVariable("id") UUID id);
@PostMapping("/api/accounts/operations") void apply(@RequestBody Operation request);
}
