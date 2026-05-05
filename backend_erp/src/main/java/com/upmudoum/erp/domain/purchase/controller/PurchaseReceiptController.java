package com.upmudoum.erp.domain.purchase.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.purchase.dto.PurchaseReceiptItemResponse;
import com.upmudoum.erp.domain.purchase.dto.PurchaseReceiptRequest;
import com.upmudoum.erp.domain.purchase.service.PurchaseReceiptService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/purchases")
public class PurchaseReceiptController {

    private final PurchaseReceiptService purchaseReceiptService;

    public PurchaseReceiptController(PurchaseReceiptService purchaseReceiptService) {
        this.purchaseReceiptService = purchaseReceiptService;
    }

    @PostMapping("/receipts")
    public ApiResponse<PurchaseReceiptItemResponse> receive(@Valid @RequestBody PurchaseReceiptRequest request) {
        return ApiResponse.ok(PurchaseReceiptItemResponse.from(purchaseReceiptService.receive(
                request.getPartnerId(), request.getItemId(), request.getWarehouseId(), request.getLotNo(),
                request.getQuantity(), request.getUnitPrice(), request.getPurchaseDate())));
    }

    @GetMapping("/receipts/items")
    public ApiResponse<List<PurchaseReceiptItemResponse>> findReceiptItems(@RequestParam Long itemId) {
        return ApiResponse.ok(purchaseReceiptService.findReceiptItems(itemId));
    }
}
