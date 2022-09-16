package bca.mbb.repository;

import bca.mbb.dto.FoTransactionHeadersDto;
import bca.mbb.entity.FoTransactionHeaderEntity;
import bca.mbb.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface FoTransactionHeaderRepository extends JpaRepository<FoTransactionHeaderEntity, String> {
    FoTransactionHeaderEntity findByChainingIdAndTransactionName(String chainingID, String transactionName);

    FoTransactionHeaderEntity findByFoTransactionHeaderId(String FoTransactionHeaderId);

    FoTransactionHeaderEntity findByTransactionHeaderId(String transactionHeaderId);

    FoTransactionHeaderEntity findByReferenceNumber(String referenceNumber);

    @Query(value = "select LPAD(LPAD(CHANNEL_REFNO_SEQUENCE.nextVal,3,'0'), 18, :prefix || to_char(SYSDATE ,'YYmmddHHMISS')) from dual", nativeQuery = true)
    String getChannelRefnoSequence(String prefix);
}
