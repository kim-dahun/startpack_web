package com.upmudoum.erp.domain.batch.service;

import com.upmudoum.erp.domain.batch.dto.ErpBatchDefinitionRequest;
import com.upmudoum.erp.domain.batch.dto.ErpBatchDefinitionResponse;
import com.upmudoum.erp.domain.batch.entity.ErpBatchDefinition;
import com.upmudoum.erp.domain.batch.repository.ErpBatchDefinitionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ErpBatchDefinitionService {

    private final ErpBatchDefinitionRepository repository;

    public ErpBatchDefinitionService(ErpBatchDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ErpBatchDefinitionResponse save(ErpBatchDefinitionRequest request) {
        ErpBatchDefinition definition = repository.findByCode(request.getCode())
                .orElseGet(() -> new ErpBatchDefinition(request.getCode(), request.getDescription(),
                        request.getTriggerPolicy(), request.isRequired()));
        definition.update(request.getDescription(), request.getTriggerPolicy(), request.isRequired());
        return ErpBatchDefinitionResponse.from(repository.save(definition));
    }

    public List<ErpBatchDefinitionResponse> findAll() {
        return repository.findAll().stream()
                .map(ErpBatchDefinitionResponse::from)
                .toList();
    }
}
