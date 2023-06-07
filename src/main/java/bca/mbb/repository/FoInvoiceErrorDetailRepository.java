package bca.mbb.repository;

import lib.fo.entity.FoInvoiceErrorDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoInvoiceErrorDetailRepository extends JpaRepository<FoInvoiceErrorDetailEntity, String> {
    List<FoInvoiceErrorDetailEntity> findByChainingId (String chainingId);
}
