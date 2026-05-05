package com.upmudoum.user.domain.userposition.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.userposition.UserPosition;
import com.upmudoum.user.domain.userposition.UserPositionRepository;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionPrimaryYnRequest;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionRequest;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPositionManagementService {

    private final UserPositionRepository userPositionRepository;

    public UserPositionManagementService(UserPositionRepository userPositionRepository) {
        this.userPositionRepository = userPositionRepository;
    }

    @Transactional(readOnly = true)
    public List<UserPositionResponse> userPositions(String comCd, String userId) {
        return userPositionRepository.findByComCdAndUserIdOrderByPrimaryYnDescPositionIdAsc(comCd, userId).stream().map(this::toUserPosition).toList();
    }

    @Transactional(readOnly = true)
    public List<UserPositionResponse> departmentMembers(String comCd, String departmentId) {
        return userPositionRepository.findByComCdAndDepartmentIdOrderByPositionIdAscUserIdAsc(comCd, departmentId).stream().map(this::toUserPosition).toList();
    }

    @Transactional
    public BulkResultDto saveUserPositions(BulkRequestDto<UserPositionRequest> request) {
        request.getAdded().forEach(this::upsertUserPosition);
        request.getUpdated().forEach(this::upsertUserPosition);
        request.getDeleted().forEach(this::deleteUserPosition);
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    @Transactional
    public BulkResultDto saveDepartmentMembers(BulkRequestDto<UserPositionRequest> request) {
        return saveUserPositions(request);
    }

    @Transactional
    public UserPositionResponse updatePrimaryYn(UserPositionPrimaryYnRequest request) {
        UserPosition userPosition = userPositionRepository
                .findByComCdAndUserIdAndDepartmentIdAndPositionId(request.getComCd(), request.getUserId(), request.getDepartmentId(), request.getPositionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User position was not found."));
        userPosition.updatePrimaryYn(request.isPrimaryYn());
        return toUserPosition(userPosition);
    }

    @Transactional
    public void syncDepartmentHead(
            String comCd,
            String departmentId,
            String previousUserId,
            String previousPositionId,
            String currentUserId,
            String currentPositionId,
            boolean enabled
    ) {
        if (hasUserPositionKey(previousUserId, previousPositionId)
                && !sameUserPosition(previousUserId, previousPositionId, currentUserId, currentPositionId)) {
            userPositionRepository.deleteByComCdAndUserIdAndDepartmentIdAndPositionId(comCd, previousUserId, departmentId, previousPositionId);
        }
        if (hasUserPositionKey(currentUserId, currentPositionId)) {
            upsertUserPosition(new UserPositionRequest(comCd, null, currentUserId, departmentId, currentPositionId, false, enabled));
        }
    }

    @Transactional
    public void deleteDepartmentPositions(String comCd, String departmentId) {
        userPositionRepository.deleteByComCdAndDepartmentId(comCd, departmentId);
    }

    private void upsertUserPosition(UserPositionRequest item) {
        String userPositionId = effectiveUserPositionId(item);
        UserPosition userPosition = userPositionRepository.findByComCdAndUserIdAndDepartmentIdAndPositionId(item.getComCd(), item.getUserId(), item.getDepartmentId(), item.getPositionId())
                .orElseGet(() -> new UserPosition(userPositionId, item.getComCd(), item.getUserId(), item.getDepartmentId(), item.getPositionId()));
        userPosition.updateMapping(userPositionId, item.getUserId(), item.getDepartmentId(), item.getPositionId(), item.isEnabled());
        userPositionRepository.save(userPosition);
    }

    private void deleteUserPosition(UserPositionRequest item) {
        userPositionRepository.deleteByComCdAndUserIdAndDepartmentIdAndPositionId(item.getComCd(), item.getUserId(), item.getDepartmentId(), item.getPositionId());
    }

    private String effectiveUserPositionId(UserPositionRequest item) {
        return item.getComCd() + "_" + item.getDepartmentId() + "_" + item.getPositionId() + "_" + item.getUserId();
    }

    private boolean hasUserPositionKey(String userId, String positionId) {
        return userId != null && !userId.isBlank() && positionId != null && !positionId.isBlank();
    }

    private boolean sameUserPosition(String previousUserId, String previousPositionId, String currentUserId, String currentPositionId) {
        return previousUserId != null
                && previousUserId.equals(currentUserId)
                && previousPositionId != null
                && previousPositionId.equals(currentPositionId);
    }

    private UserPositionResponse toUserPosition(UserPosition userPosition) {
        return new UserPositionResponse(userPosition.getComCd(), userPosition.getUserPositionId(), userPosition.getUserId(), userPosition.getDepartmentId(), userPosition.getPositionId(), userPosition.isPrimaryYn(), userPosition.isEnabled());
    }
}
