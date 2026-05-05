package com.upmudoum.user.domain.position.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.position.Position;
import com.upmudoum.user.domain.position.PositionRepository;
import com.upmudoum.user.domain.position.PositionType;
import com.upmudoum.user.domain.position.dto.PositionDtos.PositionRequest;
import com.upmudoum.user.domain.position.dto.PositionDtos.PositionResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PositionManagementService {

    private final PositionRepository positionRepository;

    public PositionManagementService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Transactional(readOnly = true)
    public List<PositionResponse> positions(String comCd) {
        return positionRepository.findByComCdOrderBySortSeqAscPositionIdAsc(comCd).stream().map(this::toPosition).toList();
    }

    @Transactional
    public BulkResultDto savePositions(BulkRequestDto<PositionRequest> request) {
        request.getAdded().forEach(item -> {
            Position position = new Position(item.getComCd(), item.getPositionId(), item.getPositionName());
            position.update(item.getPositionName(), toPositionType(item.getPositionType()), item.getSortSeq(), item.isEnabled());
            positionRepository.save(position);
        });
        request.getUpdated().forEach(item -> positionRepository.findByComCdAndPositionId(item.getComCd(), item.getPositionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Position was not found."))
                .update(item.getPositionName(), toPositionType(item.getPositionType()), item.getSortSeq(), item.isEnabled()));
        request.getDeleted().forEach(item -> positionRepository.deleteByComCdAndPositionId(item.getComCd(), item.getPositionId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private PositionType toPositionType(String value) {
        if (value == null || value.isBlank()) {
            return PositionType.CUSTOM;
        }
        try {
            return PositionType.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported positionType.");
        }
    }

    private PositionResponse toPosition(Position position) {
        return new PositionResponse(position.getComCd(), position.getPositionId(), position.getPositionName(), position.getPositionType().name(), position.getSortSeq(), position.isEnabled());
    }
}
