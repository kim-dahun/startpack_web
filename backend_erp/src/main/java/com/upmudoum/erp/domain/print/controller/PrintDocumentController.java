package com.upmudoum.erp.domain.print.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.print.dto.PrintBarcodeRequest;
import com.upmudoum.erp.domain.print.dto.PrintBarcodeResponse;
import com.upmudoum.erp.domain.print.dto.PrintDocumentResponse;
import com.upmudoum.erp.domain.print.service.PrintDocumentService;
import com.upmudoum.erp.domain.print.vo.PrintDocumentType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/prints")
public class PrintDocumentController {

    private final PrintDocumentService printDocumentService;

    public PrintDocumentController(PrintDocumentService printDocumentService) {
        this.printDocumentService = printDocumentService;
    }

    @GetMapping("/work-instructions/{documentKey}")
    public ApiResponse<PrintDocumentResponse> findWorkInstruction(@PathVariable String documentKey) {
        return ApiResponse.ok(printDocumentService.findByDocumentKey(PrintDocumentType.WORK_INSTRUCTION, documentKey));
    }

    @GetMapping("/work-instructions/barcode/{barcodeValue}")
    public ApiResponse<PrintDocumentResponse> findWorkInstructionByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.ok(printDocumentService.findByBarcode(barcodeValue));
    }

    @GetMapping("/issue-slips/{documentKey}")
    public ApiResponse<PrintDocumentResponse> findIssueSlip(@PathVariable String documentKey) {
        return ApiResponse.ok(printDocumentService.findByDocumentKey(PrintDocumentType.ISSUE_SLIP, documentKey));
    }

    @GetMapping("/issue-slips/barcode/{barcodeValue}")
    public ApiResponse<PrintDocumentResponse> findIssueSlipByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.ok(printDocumentService.findByBarcode(barcodeValue));
    }

    @GetMapping("/transaction-statements/{documentKey}")
    public ApiResponse<PrintDocumentResponse> findTransactionStatement(@PathVariable String documentKey) {
        return ApiResponse.ok(printDocumentService.findByDocumentKey(PrintDocumentType.TRANSACTION_STATEMENT, documentKey));
    }

    @GetMapping("/transaction-statements/barcode/{barcodeValue}")
    public ApiResponse<PrintDocumentResponse> findTransactionStatementByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.ok(printDocumentService.findByBarcode(barcodeValue));
    }

    @GetMapping("/purchase-orders/{documentKey}")
    public ApiResponse<PrintDocumentResponse> findPurchaseOrder(@PathVariable String documentKey) {
        return ApiResponse.ok(printDocumentService.findByDocumentKey(PrintDocumentType.PURCHASE_ORDER, documentKey));
    }

    @GetMapping("/purchase-orders/barcode/{barcodeValue}")
    public ApiResponse<PrintDocumentResponse> findPurchaseOrderByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.ok(printDocumentService.findByBarcode(barcodeValue));
    }

    @GetMapping("/goods-receipts/{documentKey}")
    public ApiResponse<PrintDocumentResponse> findGoodsReceipt(@PathVariable String documentKey) {
        return ApiResponse.ok(printDocumentService.findByDocumentKey(PrintDocumentType.GOODS_RECEIPT, documentKey));
    }

    @GetMapping("/goods-receipts/barcode/{barcodeValue}")
    public ApiResponse<PrintDocumentResponse> findGoodsReceiptByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.ok(printDocumentService.findByBarcode(barcodeValue));
    }

    @GetMapping("/barcodes/{barcodeValue}")
    public ApiResponse<PrintDocumentResponse> findByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.ok(printDocumentService.findByBarcode(barcodeValue));
    }

    @PostMapping("/barcodes")
    public ApiResponse<PrintBarcodeResponse> createBarcode(@Valid @RequestBody PrintBarcodeRequest request) {
        return ApiResponse.ok(printDocumentService.createBarcode(request));
    }

    @GetMapping("/barcodes")
    public ApiResponse<List<PrintBarcodeResponse>> findBarcodes(
            @RequestParam(required = false) PrintDocumentType documentType) {
        return ApiResponse.ok(printDocumentService.findBarcodes(documentType));
    }

    @PutMapping("/barcodes/{id}")
    public ApiResponse<PrintBarcodeResponse> updateBarcode(@PathVariable Long id,
                                                           @Valid @RequestBody PrintBarcodeRequest request) {
        return ApiResponse.ok(printDocumentService.updateBarcode(id, request));
    }

    @DeleteMapping("/barcodes/{id}")
    public ApiResponse<Void> disableBarcode(@PathVariable Long id) {
        printDocumentService.disableBarcode(id);
        return ApiResponse.ok(null);
    }
}
