package com.upmudoum.erp.domain.partner.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.partner.dto.PartnerRequest;
import com.upmudoum.erp.domain.partner.dto.PartnerResponse;
import com.upmudoum.erp.domain.partner.entity.Partner;
import com.upmudoum.erp.domain.partner.querydsl.PartnerQueryRepository;
import com.upmudoum.erp.domain.partner.repository.PartnerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerQueryRepository partnerQueryRepository;

    public PartnerService(PartnerRepository partnerRepository, PartnerQueryRepository partnerQueryRepository) {
        this.partnerRepository = partnerRepository;
        this.partnerQueryRepository = partnerQueryRepository;
    }

    @Transactional
    public PartnerResponse create(PartnerRequest request) {
        if (partnerRepository.existsByCodeValue(request.getCode())) {
            throw new BusinessException("Partner code already exists");
        }
        Partner partner = new Partner(request.getCode(), request.getName(), request.getBusinessNumber(), request.getPartnerType());
        partner.update(request.getName(), request.getBusinessNumber(), request.getPartnerType(), request.getStatus());
        return PartnerResponse.from(partnerRepository.save(partner));
    }

    public List<PartnerResponse> findAll() {
        return partnerRepository.findAll().stream().map(PartnerResponse::from).toList();
    }

    public List<PartnerResponse> search(String partnerType, com.upmudoum.erp.domain.partner.vo.PartnerStatus status,
                                        String keyword) {
        return partnerQueryRepository.search(partnerType, status, keyword).stream()
                .map(PartnerResponse::from)
                .toList();
    }

    @Transactional
    public PartnerResponse update(Long id, PartnerRequest request) {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new BusinessException("Partner not found"));
        partner.update(request.getName(), request.getBusinessNumber(), request.getPartnerType(), request.getStatus());
        return PartnerResponse.from(partner);
    }
}
