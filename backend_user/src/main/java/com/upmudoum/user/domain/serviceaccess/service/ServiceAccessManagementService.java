package com.upmudoum.user.domain.serviceaccess.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.serviceaccess.ServiceId;
import com.upmudoum.user.domain.serviceaccess.UserServiceAccess;
import com.upmudoum.user.domain.serviceaccess.UserServiceAccessRepository;
import com.upmudoum.user.domain.serviceaccess.dto.ServiceAccessDtos.ServiceAccessRequest;
import com.upmudoum.user.domain.serviceaccess.dto.ServiceAccessDtos.ServiceAccessResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceAccessManagementService {

    private final UserServiceAccessRepository serviceAccessRepository;

    public ServiceAccessManagementService(UserServiceAccessRepository serviceAccessRepository) {
        this.serviceAccessRepository = serviceAccessRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceAccessResponse> serviceAccesses(String comCd, String userId) {
        return serviceAccessRepository.findByComCdAndUserIdOrderByServiceIdAsc(comCd, userId).stream().map(this::toServiceAccess).toList();
    }

    @Transactional
    public BulkResultDto saveServiceAccesses(BulkRequestDto<ServiceAccessRequest> request) {
        request.getAdded().forEach(this::upsertServiceAccess);
        request.getUpdated().forEach(this::upsertServiceAccess);
        request.getDeleted().forEach(item -> serviceAccessRepository.deleteByComCdAndUserIdAndServiceId(item.getComCd(), item.getUserId(), toServiceId(item.getServiceId())));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private void upsertServiceAccess(ServiceAccessRequest item) {
        ServiceId serviceId = toServiceId(item.getServiceId());
        UserServiceAccess serviceAccess = serviceAccessRepository.findByComCdAndUserIdAndServiceId(item.getComCd(), item.getUserId(), serviceId)
                .orElseGet(() -> new UserServiceAccess(item.getComCd(), item.getUserId(), serviceId, item.isAccessible()));
        serviceAccess.update(item.isAccessible());
        serviceAccessRepository.save(serviceAccess);
    }

    private ServiceId toServiceId(String value) {
        try {
            return ServiceId.from(value);
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported serviceId.");
        }
    }

    private ServiceAccessResponse toServiceAccess(UserServiceAccess serviceAccess) {
        return new ServiceAccessResponse(serviceAccess.getComCd(), serviceAccess.getUserId(), serviceAccess.getServiceId().name(), serviceAccess.isAccessible());
    }
}
