package bca.mbb.repository;

import bca.mbb.entity.FoTransactionDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoTransactionDetailRepository extends JpaRepository<FoTransactionDetailEntity, String> {
    Page<FoTransactionDetailEntity> findAllByFoTransactionHeaderIdAndInvoiceNumberContains(String foTransactionHeaderId, String invoiceNumber, Pageable pageable);
    List<FoTransactionDetailEntity> findAllByFoTransactionHeaderIdOrderByLineNumberAsc(String foTransactionHeaderId);
    FoTransactionDetailEntity findByTransactionDetailId(String transactionDetailId);
    FoTransactionDetailEntity findByReferenceNumber(String referenceNumber);
    List<FoTransactionDetailEntity> findAllByFoTransactionHeaderId(String foTransactionHeaderId);
    @Query("select distinct p.currency from FoTransactionDetailEntity p where p.foTransactionHeaderId = :transactionHeaderId")
    List<String> getCurrencyByFoTransactionId(String transactionHeaderId);

}