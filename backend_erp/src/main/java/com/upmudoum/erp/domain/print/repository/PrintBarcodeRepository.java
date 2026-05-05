package com.upmudoum.erp.domain.print.repository;

import com.upmudoum.erp.domain.print.entity.PrintBarcode;
import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrintBarcodeRepository extends JpaRepository<PrintBarcode, Long> {

    boolean existsByBarcodeValueAndEnabledTrue(String barcodeValue);

    Optional<PrintBarcode> findByBarcodeValue(String barcodeValue);

    Optional<PrintBarcode> findByBarcodeValueAndEnabledTrue(String barcodeValue);

    Optional<PrintBarcode> findByDocumentTypeAndDocumentKeyAndEnabledTrue(PrintDocumentType documentType, String documentKey);

    List<PrintBarcode> findByDocumentTypeAndEnabledTrueOrderByIdDesc(PrintDocumentType documentType);

    List<PrintBarcode> findByEnabledTrueOrderByIdDesc();
}
