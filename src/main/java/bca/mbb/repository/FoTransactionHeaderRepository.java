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
    @Query(value = "select CHANNEL_REFNO_SEQUENCE.nextval from dual", nativeQuery = true)
    Long getChannelRefnoSequences();
    FoTransactionHeaderEntity findFirstByReferenceNumber(String referenceNumber);
    Page<FoTransactionHeaderEntity> findAllByStatusAndTransactionName(StatusEnum status, String transactionName, Pageable pageable);
    FoTransactionHeaderEntity findByChainingIdAndTransactionName(String chainingID, String transactionName);
    FoTransactionHeaderEntity findByFoTransactionHeaderId(String FoTransactionHeaderId);
    FoTransactionHeaderEntity findByTransactionHeaderId(String transactionHeaderId);
    FoTransactionHeaderEntity findByReferenceNumber(String referenceNumber);
    @Query(value = "select LPAD(LPAD(CHANNEL_REFNO_SEQUENCE.nextVal,3,'0'), 18, :prefix || to_char(SYSDATE ,'YYmmddHHMISS')) from dual", nativeQuery = true)
    String getChannelRefnoSequence(String prefix);
    @Query(value = "select * from FO_TRANSACTION_HEADER\n" +
            "where (:chainingId is not null and CHAINING_ID = :chainingId)\n" +
            "or (:referenceNumber is not null and REFERENCE_NUMBER = :referenceNumber)", nativeQuery = true)
    FoTransactionHeaderEntity findByChainingIdOrReferenceNumber(@Param("referenceNumber") String referenceNumber,@Param("chainingId") String chainingId);

//    @Query(value = "SELECT a " +
//            "from FoTransactionHeaderDto a")
//    Page<FoTransactionHeaderDto> getListPendingAuthorization(Pageable page);

    @Query(value = " SELECT \n" +
            "\t\tTOTAL_AMOUNT AS totalAmount,\n" +
            "\t\tEFFECTIVE_DATE AS effectiveDate,\n" +
            "\t\tCASE\n" +
            "\t\t\tTRANSACTION_TYPE WHEN 'ADD' THEN 'Tambah'\n" +
            "\t\t\tWHEN 'DELETE' THEN 'Hapus'\n" +
            "\t\tEND || ' - ' ||  REMARK || ' - ' ||  TOTAL_RECORD || ' Record' AS description,\n" +
            "\t\tCASE\n" +
            "\t\t\tTRANSACTION_NAME WHEN 'UPLOAD_INVOICE' THEN 'Upload Invoice'\n" +
            "\t\tEND AS transactionName\n" +
            "\tFROM\n" +
            "\t\tFO_TRANSACTION_HEADER " +
            " WHERE STATUS ='PENDING_AUTHORIZATION' AND TRANSACTION_NAME = 'UPLOAD_INVOICE' ", nativeQuery = true)
    Page<FoTransactionHeadersDto> test(Pageable page);
}
