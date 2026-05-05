package com.upmudoum.user.domain.code.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.code.CodeGroup;
import com.upmudoum.user.domain.code.CodeGroupRepository;
import com.upmudoum.user.domain.code.CommonCode;
import com.upmudoum.user.domain.code.CommonCodeRepository;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeGroupRequest;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeGroupResponse;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeRequest;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeResponse;
import com.upmudoum.user.domain.serviceaccess.ServiceId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CodeManagementService {

    private final CodeGroupRepository codeGroupRepository;
    private final CommonCodeRepository codeRepository;

    public CodeManagementService(CodeGroupRepository codeGroupRepository, CommonCodeRepository codeRepository) {
        this.codeGroupRepository = codeGroupRepository;
        this.codeRepository = codeRepository;
    }

    @Transactional(readOnly = true)
    public List<CodeGroupResponse> codeGroups(String comCd, String serviceId) {
        return codeGroupRepository.findByComCdAndServiceIdOrderByCodeGroupIdAsc(comCd, normalizeServiceId(serviceId)).stream().map(this::toCodeGroup).toList();
    }

    @Transactional
    public BulkResultDto saveCodeGroups(BulkRequestDto<CodeGroupRequest> request) {
        request.getAdded().forEach(item -> {
            CodeGroup codeGroup = new CodeGroup(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getCodeGroupId(), item.getCodeGroupName());
            codeGroup.update(item.getCodeGroupName(), item.getDescription(), item.isEnabled());
            codeGroupRepository.save(codeGroup);
        });
        request.getUpdated().forEach(item -> codeGroupRepository.findByComCdAndServiceIdAndCodeGroupId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getCodeGroupId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "CodeGroup was not found."))
                .update(item.getCodeGroupName(), item.getDescription(), item.isEnabled()));
        request.getDeleted().forEach(item -> codeGroupRepository.deleteByComCdAndServiceIdAndCodeGroupId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getCodeGroupId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    @Transactional(readOnly = true)
    public List<CodeResponse> codes(String comCd, String serviceId, String codeGroupId) {
        return codeRepository.findByComCdAndServiceIdAndCodeGroupIdOrderBySortOrderAsc(comCd, normalizeServiceId(serviceId), codeGroupId).stream().map(this::toCode).toList();
    }

    @Transactional
    public BulkResultDto saveCodes(BulkRequestDto<CodeRequest> request) {
        request.getAdded().forEach(item -> {
            CommonCode code = new CommonCode(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getCodeGroupId(), item.getCodeId(), item.getCodeName(), item.getSortSeq());
            code.update(item.getParentCodeGroupId(), item.getParentCodeId(), item.getCodeName(), item.getSubInfo1(), item.getSubInfo2(), item.getSubInfo3(), item.getSortSeq(), item.isEnabled());
            codeRepository.save(code);
        });
        request.getUpdated().forEach(item -> codeRepository.findByComCdAndServiceIdAndCodeGroupIdAndCodeId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getCodeGroupId(), item.getCodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Code was not found."))
                .update(item.getParentCodeGroupId(), item.getParentCodeId(), item.getCodeName(), item.getSubInfo1(), item.getSubInfo2(), item.getSubInfo3(), item.getSortSeq(), item.isEnabled()));
        request.getDeleted().forEach(item -> codeRepository.deleteByComCdAndServiceIdAndCodeGroupIdAndCodeId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getCodeGroupId(), item.getCodeId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private String normalizeServiceId(String value) {
        try {
            return ServiceId.from(value).name();
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported serviceId.");
        }
    }

    private CodeGroupResponse toCodeGroup(CodeGroup codeGroup) {
        return new CodeGroupResponse(codeGroup.getComCd(), codeGroup.getServiceId(), codeGroup.getCodeGroupId(), codeGroup.getCodeGroupName(), codeGroup.getDescription(), codeGroup.isEnabled());
    }

    private CodeResponse toCode(CommonCode code) {
        return new CodeResponse(code.getComCd(), code.getServiceId(), code.getCodeGroupId(), code.getCodeId(), code.getCodeName(), code.getParentCodeGroupId(), code.getParentCodeId(), code.getSubInfo1(), code.getSubInfo2(), code.getSubInfo3(), code.getSortOrder(), code.isEnabled());
    }
}
