package com.upmudoum.erp.domain.bom.service;

import com.upmudoum.erp.common.exception.BusinessException;
import com.upmudoum.erp.domain.bom.dto.BomComponentResponse;
import com.upmudoum.erp.domain.bom.dto.BomVersionResponse;
import com.upmudoum.erp.domain.bom.entity.Bom;
import com.upmudoum.erp.domain.bom.entity.BomComponent;
import com.upmudoum.erp.domain.bom.entity.BomVersion;
import com.upmudoum.erp.domain.bom.repository.BomComponentRepository;
import com.upmudoum.erp.domain.bom.repository.BomRepository;
import com.upmudoum.erp.domain.bom.repository.BomVersionRepository;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.item.repository.ItemRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BomService {

    private final BomRepository bomRepository;
    private final BomVersionRepository bomVersionRepository;
    private final BomComponentRepository componentRepository;
    private final ItemRepository itemRepository;

    public BomService(BomRepository bomRepository, BomVersionRepository bomVersionRepository,
                      BomComponentRepository componentRepository, ItemRepository itemRepository) {
        this.bomRepository = bomRepository;
        this.bomVersionRepository = bomVersionRepository;
        this.componentRepository = componentRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public BomVersion createVersion(Long parentItemId, String versionNo, LocalDate effectiveFrom, LocalDate effectiveTo) {
        Item parentItem = itemRepository.findById(parentItemId).orElseThrow(() -> new BusinessException("Parent item not found"));
        if (!parentItem.getItemType().isProducible()) {
            throw new BusinessException("Only semi-finished or finished goods can have BOM");
        }
        Bom bom = bomRepository.findByParentItemIdAndEnabledTrue(parentItemId)
                .orElseGet(() -> bomRepository.save(new Bom(parentItem)));
        BomVersion bomVersion = bomVersionRepository.save(new BomVersion(bom, versionNo, effectiveFrom, effectiveTo));
        if (bom.getDefaultBomVersion() == null) {
            bom.setDefaultVersion(bomVersion);
        }
        return bomVersion;
    }

    @Transactional
    public BomComponent addComponent(Long bomVersionId, Long componentItemId, BigDecimal requiredQuantity, BigDecimal lossRate) {
        BomVersion bomVersion = bomVersionRepository.findById(bomVersionId)
                .orElseThrow(() -> new BusinessException("BOM version not found"));
        Item componentItem = itemRepository.findById(componentItemId)
                .orElseThrow(() -> new BusinessException("Component item not found"));
        return componentRepository.save(new BomComponent(bomVersion, componentItem, requiredQuantity, lossRate));
    }

    public List<BomVersionResponse> findVersions(Long parentItemId) {
        return bomVersionRepository.findByBomParentItemIdAndEnabledTrueOrderByEffectiveFromDescIdDesc(parentItemId).stream()
                .map(BomVersionResponse::from)
                .toList();
    }

    public List<BomComponentResponse> findComponents(Long bomVersionId) {
        return componentRepository.findByBomVersionId(bomVersionId).stream()
                .map(BomComponentResponse::from)
                .toList();
    }

    @Transactional
    public BomVersionResponse changeDefaultVersion(Long bomVersionId) {
        BomVersion bomVersion = bomVersionRepository.findById(bomVersionId)
                .orElseThrow(() -> new BusinessException("BOM version not found"));
        bomVersion.getBom().setDefaultVersion(bomVersion);
        return BomVersionResponse.from(bomVersion);
    }
}
