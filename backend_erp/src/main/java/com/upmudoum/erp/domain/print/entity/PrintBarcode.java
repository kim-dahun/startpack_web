package com.upmudoum.erp.domain.print.entity;

import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_print_barcodes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_print_barcode_value", columnNames = "barcode_value")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrintBarcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "barcode_value", nullable = false, length = 120)
    private String barcodeValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PrintDocumentType documentType;

    @Column(nullable = false, length = 120)
    private String documentKey;

    @Column(nullable = false)
    private boolean enabled = true;

    public PrintBarcode(String barcodeValue, PrintDocumentType documentType, String documentKey) {
        this.barcodeValue = barcodeValue;
        this.documentType = documentType;
        this.documentKey = documentKey;
    }

    public void update(String barcodeValue, PrintDocumentType documentType, String documentKey, boolean enabled) {
        this.barcodeValue = barcodeValue;
        this.documentType = documentType;
        this.documentKey = documentKey;
        this.enabled = enabled;
    }

    public void disable() {
        this.enabled = false;
    }
}
