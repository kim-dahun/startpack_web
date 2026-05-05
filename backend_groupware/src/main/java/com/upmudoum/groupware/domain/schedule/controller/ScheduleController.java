package com.upmudoum.groupware.domain.schedule.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upmudoum.groupware.common.RequestContextResolver;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleRequest;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleOccurrence;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleOccurrenceExclusionRequest;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleRecurrenceRequest;
import com.upmudoum.groupware.domain.schedule.entity.ScheduleItem;
import com.upmudoum.groupware.domain.schedule.entity.ScheduleOccurrenceExclusion;
import com.upmudoum.groupware.domain.schedule.entity.ScheduleRecurrenceRule;
import com.upmudoum.groupware.domain.schedule.service.ScheduleService;
import com.upmudoum.groupware.domain.schedule.vo.ScheduleScope;

@RestController
@RequestMapping("/api/groupware/schedules")
public class ScheduleController {

    private final RequestContextResolver contextResolver;
    private final ScheduleService scheduleService;

    public ScheduleController(RequestContextResolver contextResolver, ScheduleService scheduleService) {
        this.contextResolver = contextResolver;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ScheduleItem> list(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return scheduleService.list(contextResolver.resolve(comCd, userId), from, to);
    }

    @PostMapping
    public ScheduleItem create(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @Valid @RequestBody ScheduleRequest request) {
        return scheduleService.create(contextResolver.resolve(comCd, userId), request);
    }

    @GetMapping("/{scheduleId}")
    public ScheduleItem get(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId) {
        return scheduleService.get(contextResolver.resolve(comCd, userId), scheduleId);
    }

    @PatchMapping("/{scheduleId}")
    public ScheduleItem update(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleRequest request) {
        return scheduleService.update(contextResolver.resolve(comCd, userId), scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    public void delete(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId) {
        scheduleService.delete(contextResolver.resolve(comCd, userId), scheduleId);
    }

    @GetMapping("/by-project-id/{projectId}")
    public List<ScheduleItem> listByProjectId(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID projectId) {
        return scheduleService.listByProjectId(contextResolver.resolve(comCd, userId), projectId);
    }

    @GetMapping("/by-project-code/{projectCode}")
    public List<ScheduleItem> listByProjectCode(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable String projectCode) {
        return scheduleService.listByProjectCode(contextResolver.resolve(comCd, userId), projectCode);
    }

    @PostMapping("/{scheduleId}/recurrence")
    public ScheduleRecurrenceRule upsertRecurrence(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleRecurrenceRequest request) {
        return scheduleService.upsertRecurrence(contextResolver.resolve(comCd, userId), scheduleId, request);
    }

    @PatchMapping("/{scheduleId}/recurrence")
    public ScheduleRecurrenceRule updateRecurrence(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleRecurrenceRequest request) {
        return scheduleService.upsertRecurrence(contextResolver.resolve(comCd, userId), scheduleId, request);
    }

    @GetMapping("/{scheduleId}/recurrence")
    public Optional<ScheduleRecurrenceRule> getRecurrence(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId) {
        return scheduleService.getRecurrence(contextResolver.resolve(comCd, userId), scheduleId);
    }

    @DeleteMapping("/{scheduleId}/recurrence")
    public void deleteRecurrence(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId) {
        scheduleService.deleteRecurrence(contextResolver.resolve(comCd, userId), scheduleId);
    }

    @PostMapping("/{scheduleId}/occurrences/exclusions")
    public ScheduleOccurrenceExclusion excludeOccurrence(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleOccurrenceExclusionRequest request) {
        return scheduleService.excludeOccurrence(contextResolver.resolve(comCd, userId), scheduleId, request);
    }

    @GetMapping("/{scheduleId}/occurrences")
    public List<ScheduleOccurrence> listOccurrences(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @PathVariable UUID scheduleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return scheduleService.listOccurrences(contextResolver.resolve(comCd, userId), scheduleId, from, to);
    }

    @GetMapping("/search")
    public List<ScheduleItem> search(
            @RequestHeader(RequestContextResolver.COM_CD_HEADER) String comCd,
            @RequestHeader(RequestContextResolver.USER_ID_HEADER) String userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ScheduleScope scope,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return scheduleService.search(contextResolver.resolve(comCd, userId), keyword, scope, projectId, projectCode, from, to);
    }
}
