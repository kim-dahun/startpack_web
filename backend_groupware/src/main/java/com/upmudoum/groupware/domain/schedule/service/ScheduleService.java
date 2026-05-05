package com.upmudoum.groupware.domain.schedule.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleRequest;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleOccurrence;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleOccurrenceExclusionRequest;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleRecurrenceRequest;
import com.upmudoum.groupware.domain.schedule.entity.ScheduleItem;
import com.upmudoum.groupware.domain.schedule.entity.ScheduleOccurrenceExclusion;
import com.upmudoum.groupware.domain.schedule.entity.ScheduleRecurrenceRule;
import com.upmudoum.groupware.domain.schedule.repository.ScheduleOccurrenceExclusionRepository;
import com.upmudoum.groupware.domain.schedule.repository.ScheduleRecurrenceRuleRepository;
import com.upmudoum.groupware.domain.schedule.repository.ScheduleRepository;
import com.upmudoum.groupware.domain.schedule.vo.ScheduleScope;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleRecurrenceRuleRepository scheduleRecurrenceRuleRepository;
    private final ScheduleOccurrenceExclusionRepository scheduleOccurrenceExclusionRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
            ScheduleRecurrenceRuleRepository scheduleRecurrenceRuleRepository,
            ScheduleOccurrenceExclusionRepository scheduleOccurrenceExclusionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleRecurrenceRuleRepository = scheduleRecurrenceRuleRepository;
        this.scheduleOccurrenceExclusionRepository = scheduleOccurrenceExclusionRepository;
    }

    public ScheduleItem create(TenantKey tenant, ScheduleRequest request) {
        validateRange(request.getStartAt(), request.getEndAt());
        Instant now = Instant.now();
        ScheduleItem item = new ScheduleItem(
                UUID.randomUUID(),
                tenant.getComCd(),
                tenant.getUserId(),
                request.getProjectId(),
                request.getProjectCode(),
                request.getScope(),
                request.getTitle(),
                request.getMemo(),
                request.getStartAt(),
                request.getEndAt(),
                request.isAllDay(),
                now,
                now);
        return scheduleRepository.save(item);
    }

    public List<ScheduleItem> list(TenantKey tenant, LocalDateTime from, LocalDateTime to) {
        return scheduleRepository.findVisibleSchedules(
                tenant.getComCd(),
                tenant.getUserId(),
                ScheduleScope.COMPANY,
                from,
                to);
    }

    public ScheduleItem update(TenantKey tenant, UUID scheduleId, ScheduleRequest request) {
        validateRange(request.getStartAt(), request.getEndAt());
        ScheduleItem current = findOwned(tenant, scheduleId);
        ScheduleItem updated = new ScheduleItem(
                current.getId(),
                current.getComCd(),
                current.getOwnerUserId(),
                request.getProjectId(),
                request.getProjectCode(),
                request.getScope(),
                request.getTitle(),
                request.getMemo(),
                request.getStartAt(),
                request.getEndAt(),
                request.isAllDay(),
                current.getCreatedAt(),
                Instant.now());
        return scheduleRepository.save(updated);
    }

    public void delete(TenantKey tenant, UUID scheduleId) {
        ScheduleItem current = findOwned(tenant, scheduleId);
        scheduleRepository.save(current.delete(Instant.now()));
    }

    public ScheduleItem get(TenantKey tenant, UUID scheduleId) {
        ScheduleItem item = scheduleRepository.findByIdAndComCdAndDeletedYnFalse(scheduleId, tenant.getComCd())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));
        if (item.getScope() != ScheduleScope.COMPANY && !item.getOwnerUserId().equals(tenant.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "schedule is not visible");
        }
        return item;
    }

    private ScheduleItem findOwned(TenantKey tenant, UUID scheduleId) {
        return scheduleRepository.findByIdAndComCdAndOwnerUserIdAndDeletedYnFalse(
                        scheduleId,
                        tenant.getComCd(),
                        tenant.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));
    }

    private void validateRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("endAt must be after or equal to startAt");
        }
    }

    public List<ScheduleItem> listByProjectId(TenantKey tenant, UUID projectId) {
        return scheduleRepository.findByComCdAndProjectIdAndDeletedYnFalseOrderByStartAtAsc(tenant.getComCd(), projectId);
    }

    public List<ScheduleItem> listByProjectCode(TenantKey tenant, String projectCode) {
        return scheduleRepository.findByComCdAndProjectCodeAndDeletedYnFalseOrderByStartAtAsc(tenant.getComCd(), projectCode);
    }

    public ScheduleRecurrenceRule upsertRecurrence(TenantKey tenant, UUID scheduleId, ScheduleRecurrenceRequest request) {
        findOwned(tenant, scheduleId);
        int interval = Math.max(request.getIntervalValue(), 1);
        ScheduleRecurrenceRule rule = new ScheduleRecurrenceRule(
                scheduleRecurrenceRuleRepository.findByComCdAndScheduleId(tenant.getComCd(), scheduleId)
                        .map(ScheduleRecurrenceRule::getId)
                        .orElse(UUID.randomUUID()),
                tenant.getComCd(),
                scheduleId,
                request.getFrequency(),
                interval,
                request.getUntilDate(),
                request.getCountLimit(),
                Instant.now());
        return scheduleRecurrenceRuleRepository.save(rule);
    }

    public Optional<ScheduleRecurrenceRule> getRecurrence(TenantKey tenant, UUID scheduleId) {
        get(tenant, scheduleId);
        return scheduleRecurrenceRuleRepository.findByComCdAndScheduleId(tenant.getComCd(), scheduleId);
    }

    public void deleteRecurrence(TenantKey tenant, UUID scheduleId) {
        findOwned(tenant, scheduleId);
        scheduleRecurrenceRuleRepository.findByComCdAndScheduleId(tenant.getComCd(), scheduleId)
                .ifPresent(scheduleRecurrenceRuleRepository::delete);
    }

    public ScheduleOccurrenceExclusion excludeOccurrence(TenantKey tenant, UUID scheduleId, ScheduleOccurrenceExclusionRequest request) {
        findOwned(tenant, scheduleId);
        return scheduleOccurrenceExclusionRepository.findByComCdAndScheduleIdAndOccurrenceDate(
                        tenant.getComCd(),
                        scheduleId,
                        request.getOccurrenceDate())
                .orElseGet(() -> scheduleOccurrenceExclusionRepository.save(new ScheduleOccurrenceExclusion(
                        UUID.randomUUID(),
                        tenant.getComCd(),
                        scheduleId,
                        request.getOccurrenceDate(),
                        request.getReason(),
                        Instant.now())));
    }

    public List<ScheduleOccurrence> listOccurrences(TenantKey tenant, UUID scheduleId, LocalDateTime from, LocalDateTime to) {
        ScheduleItem schedule = get(tenant, scheduleId);
        ScheduleRecurrenceRule rule = scheduleRecurrenceRuleRepository.findByComCdAndScheduleId(tenant.getComCd(), scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule recurrence not found"));
        LocalDateTime cursor = schedule.getStartAt();
        Duration duration = Duration.between(schedule.getStartAt(), schedule.getEndAt());
        LocalDateTime safeFrom = from == null ? schedule.getStartAt() : from;
        LocalDateTime safeTo = to == null ? safeFrom.plusMonths(3) : to;
        int maxCount = rule.getCountLimit() == null ? 100 : Math.min(rule.getCountLimit(), 100);
        List<ScheduleOccurrence> occurrences = new ArrayList<>();
        List<java.time.LocalDate> excludedDates = scheduleOccurrenceExclusionRepository
                .findByComCdAndScheduleId(tenant.getComCd(), scheduleId)
                .stream()
                .map(ScheduleOccurrenceExclusion::getOccurrenceDate)
                .toList();
        int generated = 0;
        while (!cursor.isAfter(safeTo) && generated < maxCount) {
            if (rule.getUntilDate() != null && cursor.toLocalDate().isAfter(rule.getUntilDate())) {
                break;
            }
            LocalDateTime occurrenceEnd = cursor.plus(duration);
            if (!excludedDates.contains(cursor.toLocalDate()) && !occurrenceEnd.isBefore(safeFrom) && !cursor.isAfter(safeTo)) {
                occurrences.add(new ScheduleOccurrence(schedule.getId(), schedule.getTitle(), cursor, occurrenceEnd));
            }
            cursor = nextOccurrence(cursor, rule);
            generated++;
        }
        return occurrences;
    }

    public List<ScheduleItem> search(TenantKey tenant, String keyword, ScheduleScope scope, UUID projectId, String projectCode,
            LocalDateTime from, LocalDateTime to) {
        return list(tenant, from, to).stream()
                .filter(item -> scope == null || item.getScope() == scope)
                .filter(item -> projectId == null || projectId.equals(item.getProjectId()))
                .filter(item -> projectCode == null || projectCode.equals(item.getProjectCode()))
                .filter(item -> keyword == null || keyword.isBlank()
                        || item.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (item.getMemo() != null && item.getMemo().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
    }

    private LocalDateTime nextOccurrence(LocalDateTime cursor, ScheduleRecurrenceRule rule) {
        int interval = Math.max(rule.getIntervalValue(), 1);
        return switch (rule.getFrequency()) {
            case DAILY -> cursor.plusDays(interval);
            case WEEKLY -> cursor.plusWeeks(interval);
            case MONTHLY -> cursor.plusMonths(interval);
            case YEARLY -> cursor.plusYears(interval);
        };
    }
}
