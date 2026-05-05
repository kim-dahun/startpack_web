package com.upmudoum.user.domain.user.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.user.UserAccount;
import com.upmudoum.user.domain.user.UserAccountRepository;
import com.upmudoum.user.domain.user.UserStatus;
import com.upmudoum.user.domain.user.dto.UserDtos.UserRequest;
import com.upmudoum.user.domain.user.dto.UserDtos.UserResponse;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> users(String comCd) {
        return userRepository.findByComCdOrderByUserIdAsc(comCd).stream().map(this::toUser).toList();
    }

    @Transactional
    public BulkResultDto saveUsers(BulkRequestDto<UserRequest> request) {
        request.getAdded().forEach(this::addUser);
        request.getUpdated().forEach(this::updateUser);
        request.getDeleted().forEach(item -> userRepository.deleteByComCdAndUserId(item.getComCd(), item.getUserId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private void addUser(UserRequest item) {
        UserAccount user = new UserAccount(item.getComCd(), item.getUserId(), item.getUserName(), passwordEncoder.encode(item.getPassword()));
        user.updateProfile(item.getUserName(), item.getEmail(), item.getPhone(), item.getAddress(), item.getStatus() == null ? UserStatus.ACTIVE : item.getStatus());
        user.updateJobGrade(item.getJobGradeId());
        userRepository.save(user);
    }

    private void updateUser(UserRequest item) {
        UserAccount user = userRepository.findByComCdAndUserId(item.getComCd(), item.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User was not found."));
        user.updateProfile(item.getUserName(), item.getEmail(), item.getPhone(), item.getAddress(), item.getStatus());
        user.updateJobGrade(item.getJobGradeId());
        if (item.getPassword() != null && !item.getPassword().isBlank()) {
            user.changePasswordHash(passwordEncoder.encode(item.getPassword()));
        }
    }

    private UserResponse toUser(UserAccount user) {
        return new UserResponse(user.getComCd(), user.getUserId(), user.getUserName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getJobGradeId(), user.getStatus().name());
    }
}
