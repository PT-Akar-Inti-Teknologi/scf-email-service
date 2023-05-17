package bca.mbb.repository;

import bca.mbb.dto.FoTransactionDetailDto;
import lib.fo.entity.FoTransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoTransactionDetailRepository extends JpaRepository<FoTransactionDetailEntity, String> {
    List<FoTransactionDetailEntity> findAllByFoTransactionHeaderIdOrderByLineNumberAsc(String foTransactionHeaderId);

    FoTransactionDetailEntity findByTransactionDetailId(String transactionDetailId);

    @Query(value =  " SELECT new bca.mbb.dto.FoTransactionDetailDto(ftd.programCode, ftd.sellerCode, ftd.buyerCode)" +
                    " FROM FoTransactionHeaderEntity fth JOIN FoTransactionDetailEntity ftd " +
                    " ON fth.foTransactionHeaderId  = ftd.foTransactionHeaderId " +
                    " WHERE fth.foTransactionHeaderId = :foTransactionHeaderId " +
                    " GROUP BY ftd.programCode, ftd.sellerCode, ftd.buyerCode ")
    List<FoTransactionDetailDto> groupByProgramCodeSellerCodeBuyerCode(String foTransactionHeaderId);

    @Query("select distinct p.currency from FoTransactionDetailEntity p where p.foTransactionHeaderId = :transactionHeaderId")
    String getCurrencyByFoTransactionId(String transactionHeaderId);
}