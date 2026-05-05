package com.upmudoum.groupware.domain.directory.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.upmudoum.groupware.domain.directory.dto.UserDirectoryAffiliation;
import com.upmudoum.groupware.domain.directory.dto.UserDirectoryItem;
import com.upmudoum.groupware.domain.directory.infra.BackendUserAffiliationResponse;
import com.upmudoum.groupware.domain.directory.infra.BackendUserDirectoryClient;
import com.upmudoum.groupware.domain.directory.infra.BackendUserOrganizationUserResponse;
import com.upmudoum.groupware.domain.message.repository.MessageRepository;
import com.upmudoum.groupware.domain.project.repository.ProjectRepository;

@Service
public class UserDirectoryService {

    private final BackendUserDirectoryClient backendUserDirectoryClient;
    private final ProjectRepository projectRepository;
    private final MessageRepository messageRepository;

    public UserDirectoryService(
            BackendUserDirectoryClient backendUserDirectoryClient,
            ProjectRepository projectRepository,
            MessageRepository messageRepository) {
        this.backendUserDirectoryClient = backendUserDirectoryClient;
        this.projectRepository = projectRepository;
        this.messageRepository = messageRepository;
    }

    public List<UserDirectoryItem> search(String comCd, String keyword) {
        Optional<List<BackendUserOrganizationUserResponse>> backendUsers =
                backendUserDirectoryClient.searchOrganizationUsers(comCd, keyword);
        if (backendUsers.isPresent()) {
            return backendUsers.get().stream()
                    .map(this::toDirectoryItem)
                    .sorted(Comparator.comparing(UserDirectoryItem::getUserId))
                    .toList();
        }

        Map<String, String> users = new LinkedHashMap<>();
        projectRepository.findByComCdAndDeletedYnFalse(comCd).forEach(project -> {
            putIfMatches(users, project.getOwnerUserId(), "PROJECT", keyword);
            project.getMemberUserIds().forEach(userId -> putIfMatches(users, userId, "PROJECT", keyword));
        });
        messageRepository.findByComCd(comCd).forEach(message -> {
            putIfMatches(users, message.getSenderUserId(), "MESSAGE", keyword);
            putIfMatches(users, message.getReceiverUserId(), "MESSAGE", keyword);
        });
        return users.entrySet().stream()
                .map(entry -> new UserDirectoryItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(UserDirectoryItem::getUserId))
                .toList();
    }

    private UserDirectoryItem toDirectoryItem(BackendUserOrganizationUserResponse response) {
        BackendUserAffiliationResponse primaryAffiliation = primaryAffiliation(response.getAffiliations());
        return new UserDirectoryItem(
                response.getUserId(),
                response.getUserName(),
                response.getJobGradeId(),
                response.getJobGradeName(),
                primaryAffiliation == null ? null : primaryAffiliation.getDepartmentId(),
                primaryAffiliation == null ? null : primaryAffiliation.getDepartmentName(),
                primaryAffiliation == null ? null : primaryAffiliation.getPositionId(),
                primaryAffiliation == null ? null : primaryAffiliation.getPositionName(),
                affiliations(response.getAffiliations()),
                "BACKEND_USER");
    }

    private BackendUserAffiliationResponse primaryAffiliation(List<BackendUserAffiliationResponse> affiliations) {
        if (affiliations == null || affiliations.isEmpty()) {
            return null;
        }
        return affiliations.stream()
                .filter(BackendUserAffiliationResponse::isPrimaryYn)
                .findFirst()
                .orElse(affiliations.get(0));
    }

    private List<UserDirectoryAffiliation> affiliations(List<BackendUserAffiliationResponse> affiliations) {
        if (affiliations == null) {
            return List.of();
        }
        return affiliations.stream()
                .map(affiliation -> new UserDirectoryAffiliation(
                        affiliation.getDepartmentId(),
                        affiliation.getDepartmentName(),
                        affiliation.getPositionId(),
                        affiliation.getPositionName(),
                        affiliation.isPrimaryYn()))
                .toList();
    }

    private void putIfMatches(Map<String, String> users, String userId, String source, String keyword) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        if (keyword != null && !keyword.isBlank() && !userId.toLowerCase().contains(keyword.toLowerCase())) {
            return;
        }
        users.putIfAbsent(userId, source);
    }
}
