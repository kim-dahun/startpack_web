package com.upmudoum.erp.domain.process.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.process.dto.ErpProcessRequest;
import com.upmudoum.erp.domain.process.dto.ErpProcessResponse;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.process.repository.ErpProcessRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ErpProcessService {

    private final ErpProcessRepository processRepository;

    public ErpProcessService(ErpProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Transactional
    public ErpProcessResponse create(ErpProcessRequest request) {
        if (processRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Process code already exists");
        }
        return ErpProcessResponse.from(processRepository.save(new ErpProcess(
                request.getCode(), request.getName(), request.getProcessType(), request.getDescription())));
    }

    public List<ErpProcessResponse> findAll() {
        return processRepository.findByEnabledTrueOrderByCodeAsc().stream()
                .map(ErpProcessResponse::from)
                .toList();
    }

    public List<ErpProcessResponse> search(String processType) {
        if (processType == null || processType.isBlank()) {
            return findAll();
        }
        return processRepository.findByProcessTypeAndEnabledTrueOrderByCodeAsc(processType).stream()
                .map(ErpProcessResponse::from)
                .toList();
    }

    @Transactional
    public ErpProcessResponse update(Long id, ErpProcessRequest request) {
        ErpProcess process = processRepository.findById(id).orElseThrow(() -> new BusinessException("Process not found"));
        process.update(request.getName(), request.getProcessType(), request.getDescription(), request.isEnabled());
        return ErpProcessResponse.from(process);
    }
}
