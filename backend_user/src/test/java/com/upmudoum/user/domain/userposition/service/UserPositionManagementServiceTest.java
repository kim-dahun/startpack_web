package com.upmudoum.user.domain.userposition.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.domain.userposition.UserPosition;
import com.upmudoum.user.domain.userposition.UserPositionRepository;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionPrimaryYnRequest;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionRequest;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UserPositionManagementServiceTest {

    private UserPositionRepository userPositionRepository;
    private UserPositionManagementService userPositionManagementService;

    @BeforeEach
    void setUp() {
        userPositionRepository = mock(UserPositionRepository.class);
        userPositionManagementService = new UserPositionManagementService(userPositionRepository);
    }

    @Test
    void saveUserPositionsGeneratesUserPositionIdFromComDepartmentPositionUser() {
        UserPositionRequest request = new UserPositionRequest("COM001", "ignored", "kim", "FIN", "LEAD", true, true);
        when(userPositionRepository.findByComCdAndUserIdAndDepartmentIdAndPositionId("COM001", "kim", "FIN", "LEAD"))
                .thenReturn(Optional.empty());

        userPositionManagementService.saveUserPositions(new BulkRequestDto<>(List.of(request), List.of(), List.of()));

        ArgumentCaptor<UserPosition> captor = ArgumentCaptor.forClass(UserPosition.class);
        verify(userPositionRepository).save(captor.capture());
        UserPosition saved = captor.getValue();
        assertThat(saved.getUserPositionId()).isEqualTo("COM001_FIN_LEAD_kim");
        assertThat(saved.isPrimaryYn()).isFalse();
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void saveUserPositionsDeletesByNaturalMappingKey() {
        UserPositionRequest request = new UserPositionRequest("COM001", null, "kim", "FIN", "LEAD", false, true);

        userPositionManagementService.saveUserPositions(new BulkRequestDto<>(List.of(), List.of(), List.of(request)));

        verify(userPositionRepository).deleteByComCdAndUserIdAndDepartmentIdAndPositionId("COM001", "kim", "FIN", "LEAD");
    }

    @Test
    void updatePrimaryYnOnlyUpdatesPrimaryYn() {
        UserPosition userPosition = new UserPosition("COM001_FIN_LEAD_kim", "COM001", "kim", "FIN", "LEAD");
        when(userPositionRepository.findByComCdAndUserIdAndDepartmentIdAndPositionId("COM001", "kim", "FIN", "LEAD"))
                .thenReturn(Optional.of(userPosition));

        UserPositionResponse response = userPositionManagementService.updatePrimaryYn(
                new UserPositionPrimaryYnRequest("COM001", "kim", "FIN", "LEAD", true)
        );

        assertThat(response.isPrimaryYn()).isTrue();
        assertThat(userPosition.getUserPositionId()).isEqualTo("COM001_FIN_LEAD_kim");
    }

    @Test
    void syncDepartmentHeadDeletesPreviousHeadAndUpsertsCurrentHead() {
        when(userPositionRepository.findByComCdAndUserIdAndDepartmentIdAndPositionId("COM001", "kim", "FIN", "LEAD"))
                .thenReturn(Optional.empty());

        userPositionManagementService.syncDepartmentHead("COM001", "FIN", "lee", "HEAD", "kim", "LEAD", true);

        verify(userPositionRepository).deleteByComCdAndUserIdAndDepartmentIdAndPositionId("COM001", "lee", "FIN", "HEAD");
        ArgumentCaptor<UserPosition> captor = ArgumentCaptor.forClass(UserPosition.class);
        verify(userPositionRepository).save(captor.capture());
        assertThat(captor.getValue().getUserPositionId()).isEqualTo("COM001_FIN_LEAD_kim");
    }
}
