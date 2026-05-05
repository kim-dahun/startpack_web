package com.upmudoum.erp.domain.accounting.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.accounting.dto.AccountingVoucherRequest;
import com.upmudoum.erp.domain.accounting.dto.AccountingVoucherResponse;
import com.upmudoum.erp.domain.accounting.entity.AccountingVoucher;
import com.upmudoum.erp.domain.accounting.repository.AccountingVoucherRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccountingEventService {

    private final AccountingVoucherRepository voucherRepository;

    public AccountingEventService(AccountingVoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Transactional
    public AccountingVoucherResponse create(AccountingVoucherRequest request) {
        if (voucherRepository.existsByVoucherNo(request.getVoucherNo())) {
            throw new BusinessException("Voucher number already exists");
        }
        if (request.getSourceEventType() != null && request.getSourceEventId() != null
                && voucherRepository.existsBySourceEventTypeAndSourceEventId(
                request.getSourceEventType(), request.getSourceEventId())) {
            throw new BusinessException("Voucher for source event already exists");
        }
        AccountingVoucher voucher = new AccountingVoucher(request.getVoucherNo(), request.getVoucherDate(),
                request.getAmount(), request.getCurrencyCode(), request.getSourceEventType(), request.getSourceEventId());
        if (request.isPostImmediately()) {
            voucher.post();
        }
        return AccountingVoucherResponse.from(voucherRepository.save(voucher));
    }

    @Transactional
    public AccountingVoucherResponse post(Long id) {
        AccountingVoucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Voucher not found"));
        voucher.post();
        return AccountingVoucherResponse.from(voucher);
    }

    public List<AccountingVoucherResponse> findBySource(String sourceEventType, String sourceEventId) {
        return voucherRepository.findBySourceEventTypeAndSourceEventIdOrderByIdDesc(sourceEventType, sourceEventId).stream()
                .map(AccountingVoucherResponse::from)
                .toList();
    }
}
