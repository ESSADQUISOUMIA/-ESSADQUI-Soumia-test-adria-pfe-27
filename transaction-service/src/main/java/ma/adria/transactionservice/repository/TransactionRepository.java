package ma.adria.transactionservice.repository;
import ma.adria.transactionservice.domain.BankTransaction;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import jakarta.persistence.LockModeType;import java.util.*;
public interface TransactionRepository extends JpaRepository<BankTransaction,UUID> {
List<BankTransaction> findTop100ByStatusAndPublishedFalseOrderByCreatedAtAsc(String status);
@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from BankTransaction t where t.id=:id") Optional<BankTransaction> lock(@Param("id") UUID id);
}
