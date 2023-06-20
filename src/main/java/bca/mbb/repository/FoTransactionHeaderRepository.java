package bca.mbb.repository;

import bca.mbb.dto.FoTransactionHeaderDto;
import bca.mbb.dto.TransactionHeaderDto;
import lib.fo.entity.FoTransactionHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FoTransactionHeaderRepository extends JpaRepository<FoTransactionHeaderEntity, String> {
    FoTransactionHeaderEntity findByChainingIdAndTransactionName(String chainingID, String transactionName);

    FoTransactionHeaderEntity findByFoTransactionHeaderId(String foTransactionHeaderId);

    FoTransactionHeaderEntity findByTransactionHeaderId(String transactionHeaderId);

    @Query(value= "SELECT fth.WORKFLOW_FAILURE FROM FO_TRANSACTION_HEADER fth WHERE CHAINING_ID = :chainingId", nativeQuery = true)
    String getWorkflowFailure(String chainingId);

    @Modifying
    @Query(value =
            "update FO_TRANSACTION_HEADER \n" +
                    "set WORKFLOW_FAILURE = :workflowFailure, UPDATED_DATE = trunc(sysdate)  \n" +
                    "WHERE CHAINING_ID = :chainingId", nativeQuery = true)
    @Transactional
    void updateWorkflowFailure(@Param("workflowFailure") String workflowFailure, @Param("chainingId") String chainingId);

    @Query(value = " SELECT new bca.mbb.dto.FoTransactionHeaderDto(p) " +
                   " FROM FoTransactionHeaderEntity p " +
                   " WHERE p.foTransactionHeaderId = :transactionHeaderId")
    FoTransactionHeaderDto getCounterparty(String transactionHeaderId);
}
