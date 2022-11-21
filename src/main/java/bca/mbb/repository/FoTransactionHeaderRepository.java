package bca.mbb.repository;

import lib.fo.entity.FoTransactionHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface FoTransactionHeaderRepository extends JpaRepository<FoTransactionHeaderEntity, String> {
    FoTransactionHeaderEntity findByChainingIdAndTransactionName(String chainingID, String transactionName);

    FoTransactionHeaderEntity findByFoTransactionHeaderId(String foTransactionHeaderId);

    FoTransactionHeaderEntity findByTransactionHeaderId(String transactionHeaderId);

    FoTransactionHeaderEntity findByReferenceNumber(String referenceNumber);

    @Query(value = "select LPAD(LPAD(CHANNEL_REFNO_SEQUENCE.nextVal,3,'0'), 18, :prefix || to_char(SYSDATE ,'YYmmddHHMISS')) from dual", nativeQuery = true)
    String getChannelRefnoSequence(String prefix);
}
