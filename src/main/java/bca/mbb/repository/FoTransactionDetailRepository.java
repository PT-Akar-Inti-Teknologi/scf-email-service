package bca.mbb.repository;

import lib.fo.entity.FoTransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoTransactionDetailRepository extends JpaRepository<FoTransactionDetailEntity, String> {
    List<FoTransactionDetailEntity> findAllByFoTransactionHeaderIdOrderByLineNumberAsc(String foTransactionHeaderId);

    FoTransactionDetailEntity findByTransactionDetailId(String transactionDetailId);

    @Query("select distinct p.currency from FoTransactionDetailEntity p where p.foTransactionHeaderId = :transactionHeaderId")
    String getCurrencyByFoTransactionId(String transactionHeaderId);
}