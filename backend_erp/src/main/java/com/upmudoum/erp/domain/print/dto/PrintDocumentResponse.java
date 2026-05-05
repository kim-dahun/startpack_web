package com.upmudoum.erp.domain.print.dto;

import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrintDocumentResponse {

    private PrintDocumentType documentType;
    private String documentKey;
    private String barcodeValue;
    private String title;
    private Map<String, Object> header;
    private List<Map<String, Object>> lines;
    private Map<String, Object> summary;

    public PrintDocumentResponse(PrintDocumentType documentType, String documentKey, String barcodeValue, String title,
                                 Map<String, Object> header, List<Map<String, Object>> lines,
                                 Map<String, Object> summary) {
        this.documentType = documentType;
        this.documentKey = documentKey;
        this.barcodeValue = barcodeValue;
        this.title = title;
        this.header = header;
        this.lines = lines;
        this.summary = summary;
    }
}
