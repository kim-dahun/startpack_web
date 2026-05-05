package com.upmudoum.groupware;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.upmudoum.groupware.common.GroupwareException;
import com.upmudoum.groupware.common.vo.TenantKey;
import com.upmudoum.groupware.domain.approval.dto.ApprovalDocumentRequest;
import com.upmudoum.groupware.domain.approval.dto.ApprovalLineRequest;
import com.upmudoum.groupware.domain.approval.dto.ApprovalLineTemplateRequest;
import com.upmudoum.groupware.domain.approval.entity.ApprovalDocument;
import com.upmudoum.groupware.domain.approval.entity.ApprovalStatus;
import com.upmudoum.groupware.domain.approval.repository.ApprovalDocumentRepository;
import com.upmudoum.groupware.domain.approval.service.ApprovalService;
import com.upmudoum.groupware.domain.approval.vo.ApprovalDecisionMode;
import com.upmudoum.groupware.domain.approval.vo.ApprovalLineStage;
import com.upmudoum.groupware.domain.approval.vo.ApprovalRoleType;
import com.upmudoum.groupware.domain.approval.vo.ApprovalTargetType;
import com.upmudoum.groupware.domain.chat.dto.CreateChatRoomRequest;
import com.upmudoum.groupware.domain.chat.dto.SendChatMessageRequest;
import com.upmudoum.groupware.domain.chat.service.ChatService;
import com.upmudoum.groupware.domain.chat.vo.ChatRoomType;
import com.upmudoum.groupware.domain.cost.dto.CostAccountRequest;
import com.upmudoum.groupware.domain.cost.dto.CostItemRequest;
import com.upmudoum.groupware.domain.cost.dto.ScheduleCostRequest;
import com.upmudoum.groupware.domain.cost.service.CostService;
import com.upmudoum.groupware.domain.message.dto.SendMessageRequest;
import com.upmudoum.groupware.domain.message.dto.MessageAttachmentRequest;
import com.upmudoum.groupware.domain.message.repository.MessageRepository;
import com.upmudoum.groupware.domain.message.service.MessageService;
import com.upmudoum.groupware.domain.notification.dto.CreateNotificationRequest;
import com.upmudoum.groupware.domain.notification.entity.NotificationStatus;
import com.upmudoum.groupware.domain.notification.repository.NotificationRepository;
import com.upmudoum.groupware.domain.notification.service.NotificationService;
import com.upmudoum.groupware.domain.project.dto.ProjectCommentRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectRequest;
import com.upmudoum.groupware.domain.project.dto.ProjectTaskRequest;
import com.upmudoum.groupware.domain.project.repository.ProjectRepository;
import com.upmudoum.groupware.domain.project.service.ProjectService;
import com.upmudoum.groupware.domain.project.vo.ProjectStatus;
import com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleRequest;
import com.upmudoum.groupware.domain.schedule.dto.ScheduleRecurrenceRequest;
import com.upmudoum.groupware.domain.schedule.repository.ScheduleRepository;
import com.upmudoum.groupware.domain.schedule.service.ScheduleService;
import com.upmudoum.groupware.domain.schedule.vo.RecurrenceFrequency;
import com.upmudoum.groupware.domain.schedule.vo.ScheduleScope;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:groupwaretest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS groupware_service\\\\;SET SCHEMA groupware_service",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.hikari.schema=groupware_service",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=groupware_service",
        "spring.jpa.show-sql=false"
})
@Transactional
class GroupwareServiceTests {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private CostService costService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ApprovalDocumentRepository approvalDocumentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void jpaRepositoriesAreLoaded() {
        assertThat(notificationRepository).isNotNull();
        assertThat(messageRepository).isNotNull();
        assertThat(scheduleRepository).isNotNull();
        assertThat(approvalDocumentRepository).isNotNull();
        assertThat(projectRepository).isNotNull();
    }

    @Test
    void jpaTablesAreCreatedOnlyInGroupwareServiceSchema() {
        Integer groupwareTableCount = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.tables
                where lower(table_schema) = 'groupware_service'
                  and lower(table_name) in (
                    'gw_notification',
                    'gw_message',
                    'gw_schedule',
                    'gw_approval_document',
                    'gw_approval_document_approver_user_ids',
                    'gw_project',
                    'gw_project_member_user_ids',
                    'gw_project_reference_user_ids',
                    'gw_chat_room',
                    'gw_chat_room_member',
                    'gw_chat_message',
                    'gw_cost_item',
                    'gw_cost_account',
                    'gw_schedule_cost',
                    'gw_approval_line',
                    'gw_approval_action_history',
                    'gw_message_attachment',
                    'gw_project_task',
                    'gw_project_comment',
                    'gw_schedule_recurrence_rule',
                    'gw_schedule_occurrence_exclusion',
                    'gw_approval_line_template',
                    'gw_approval_line_template_item'
                  )
                """, Integer.class);
        Integer publicTableCount = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.tables
                where lower(table_schema) = 'public'
                  and lower(table_name) like 'gw_%'
                """, Integer.class);

        assertThat(groupwareTableCount).isEqualTo(23);
        assertThat(publicTableCount).isZero();
    }

    @Test
    void chatRoomSeparatesDirectAndGroupAndTracksReadPosition() {
        TenantKey user1 = new TenantKey("COM1", "user1");
        var direct = chatService.createRoom(user1, new CreateChatRoomRequest(
                ChatRoomType.DIRECT,
                null,
                List.of("user2")));
        var duplicate = chatService.createRoom(user1, new CreateChatRoomRequest(
                ChatRoomType.DIRECT,
                null,
                List.of("user2")));

        assertThat(duplicate.getId()).isEqualTo(direct.getId());

        var message = chatService.sendMessage(user1, direct.getId(), new SendChatMessageRequest(null, "hello room"));
        assertThat(chatService.unreadCount(new TenantKey("COM1", "user2"), direct.getId()).get("count")).isEqualTo(1);
        var read = chatService.updateRead(new TenantKey("COM1", "user2"), direct.getId(), message.getId());
        var group = chatService.createRoom(user1, new CreateChatRoomRequest(
                ChatRoomType.GROUP,
                "project room",
                List.of("user2", "user3")));
        chatService.addMember(user1, group.getId(), "user4");

        assertThat(read.getLastReadMessageId()).isEqualTo(message.getId());
        assertThat(chatService.unreadCount(new TenantKey("COM1", "user2"), direct.getId()).get("count")).isZero();
        assertThat(chatService.listMessages(user1, direct.getId(), 0, 10).getItems()).hasSize(1);
        assertThat(chatService.listMembers(user1, group.getId())).extracting("userId").contains("user1", "user2", "user3", "user4");
    }

    @Test
    void notificationCanBeCreatedAndReadThroughJpa() {
        TenantKey admin = new TenantKey("COM1", "admin");
        TenantKey target = new TenantKey("COM1", "user1");

        var notification = notificationService.create(admin, new CreateNotificationRequest(
                "user1",
                "notice",
                "body",
                null,
                null));

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.UNREAD);
        assertThat(notificationRepository.existsById(notification.getId())).isTrue();
        assertThat(notificationService.countUnread(target)).isEqualTo(1);

        var read = notificationService.markRead(target, notification.getId());

        assertThat(read.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(notificationService.countUnread(target)).isZero();
    }

    @Test
    void messageConversationIsPagedByTenantAndPeerThroughJpa() {
        var sent = messageService.send(new TenantKey("COM1", "user1"), new SendMessageRequest("user2", "hello"));
        messageService.send(new TenantKey("COM2", "user1"), new SendMessageRequest("user2", "hidden"));

        var page = messageService.conversation(new TenantKey("COM1", "user2"), "user1", 0, 10);
        var attachment = messageService.addAttachment(new TenantKey("COM1", "user1"), sent.getId(),
                new MessageAttachmentRequest("memo.txt", "text/plain", 10, "messages/memo.txt"));
        var read = messageService.markConversationRead(new TenantKey("COM1", "user2"), "user1", sent.getId());

        assertThat(page.getItems()).hasSize(1);
        assertThat(page.getItems().get(0).getContent()).isEqualTo("hello");
        assertThat(page.isHasNext()).isFalse();
        assertThat(messageService.listAttachments(new TenantKey("COM1", "user2"), sent.getId())).extracting("id").containsExactly(attachment.getId());
        assertThat(read.get("readCount")).isEqualTo(1);
        assertThat(messageService.countUnread(new TenantKey("COM1", "user2"), "user1")).isZero();
        assertThat(messageRepository.count()).isEqualTo(2);
    }

    @Test
    void scheduleRejectsInvalidTimeRange() {
        ScheduleRequest request = new ScheduleRequest(
                "bad",
                null,
                LocalDateTime.of(2026, 4, 30, 10, 0),
                LocalDateTime.of(2026, 4, 30, 9, 0),
                false,
                ScheduleScope.PERSONAL);

        assertThatThrownBy(() -> scheduleService.create(new TenantKey("COM1", "user1"), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endAt");
    }

    @Test
    void scheduleSeparatesPersonalAndCompanyItemsThroughJpa() {
        UUID projectId = UUID.randomUUID();
        ScheduleRequest personal = new ScheduleRequest(
                "personal",
                projectId,
                "PRJ-1",
                null,
                LocalDateTime.of(2026, 4, 30, 9, 0),
                LocalDateTime.of(2026, 4, 30, 10, 0),
                false,
                ScheduleScope.PERSONAL);
        ScheduleRequest company = new ScheduleRequest(
                "company",
                null,
                LocalDateTime.of(2026, 4, 30, 11, 0),
                LocalDateTime.of(2026, 4, 30, 12, 0),
                false,
                ScheduleScope.COMPANY);

        scheduleService.create(new TenantKey("COM1", "user1"), personal);
        scheduleService.create(new TenantKey("COM1", "user1"), company);

        var visibleToOtherUser = scheduleService.list(new TenantKey("COM1", "user2"), null, null);

        assertThat(visibleToOtherUser).extracting("title").containsExactly("company");
        assertThat(scheduleService.listByProjectId(new TenantKey("COM1", "user1"), projectId)).extracting("title").containsExactly("personal");
        assertThat(scheduleService.listByProjectCode(new TenantKey("COM1", "user1"), "PRJ-1")).extracting("title").containsExactly("personal");
        assertThat(scheduleRepository.count()).isEqualTo(2);
    }

    @Test
    void scheduleRecurrenceRuleCanBeStored() {
        TenantKey user1 = new TenantKey("COM1", "user1");
        var schedule = scheduleService.create(user1, new ScheduleRequest(
                "daily",
                null,
                LocalDateTime.of(2026, 5, 1, 9, 0),
                LocalDateTime.of(2026, 5, 1, 10, 0),
                false,
                ScheduleScope.PERSONAL));

        var rule = scheduleService.upsertRecurrence(user1, schedule.getId(),
                new ScheduleRecurrenceRequest(RecurrenceFrequency.DAILY, 1, LocalDate.of(2026, 5, 10), 10));

        assertThat(scheduleService.getRecurrence(user1, schedule.getId())).isPresent();
        assertThat(scheduleService.listOccurrences(user1, schedule.getId(),
                LocalDateTime.of(2026, 5, 1, 0, 0),
                LocalDateTime.of(2026, 5, 3, 23, 59))).hasSize(3);
        assertThat(rule.getFrequency()).isEqualTo(RecurrenceFrequency.DAILY);
    }

    @Test
    void projectTasksAndCommentsCanBeManaged() {
        TenantKey user1 = new TenantKey("COM1", "user1");
        var project = projectService.create(user1, new ProjectRequest(
                "p",
                "desc",
                List.of("user2"),
                List.of("observer"),
                ProjectStatus.IN_PROGRESS,
                10));

        var task = projectService.createTask(user1, project.getId(), new ProjectTaskRequest(
                "task",
                "do it",
                "user2",
                LocalDate.of(2026, 5, 5),
                ProjectTaskStatus.TODO));
        var comment = projectService.createComment(user1, project.getId(), new ProjectCommentRequest(task.getId(), "note"));

        assertThat(projectService.listTasks(new TenantKey("COM1", "user2"), project.getId())).extracting("id").containsExactly(task.getId());
        assertThat(projectService.list(new TenantKey("COM1", "observer"))).extracting("id").containsExactly(project.getId());
        assertThat(projectService.listComments(user1, project.getId(), task.getId())).extracting("id").containsExactly(comment.getId());
    }

    @Test
    void scheduleCostCanBeManagedByScheduleAndProject() {
        TenantKey user1 = new TenantKey("COM1", "user1");
        UUID projectId = UUID.randomUUID();
        var schedule = scheduleService.create(user1, new ScheduleRequest(
                "project meeting",
                projectId,
                "PRJ-COST",
                null,
                LocalDateTime.of(2026, 5, 1, 9, 0),
                LocalDateTime.of(2026, 5, 1, 10, 0),
                false,
                ScheduleScope.PERSONAL));
        var item = costService.createItem(user1, new CostItemRequest("Meal", true));
        var account = costService.createAccount(user1, new CostAccountRequest("Corp Card", true));

        var cost = costService.createScheduleCost(user1, new ScheduleCostRequest(
                schedule.getId(),
                projectId,
                "PRJ-COST",
                LocalDate.of(2026, 5, 1),
                item.getId(),
                account.getId(),
                new BigDecimal("12000.00"),
                "lunch"));

        assertThat(costService.listBySchedule(user1, schedule.getId())).extracting("id").containsExactly(cost.getId());
        assertThat(costService.listByProjectId(user1, projectId)).extracting("id").containsExactly(cost.getId());
        assertThat(costService.listByProjectCode(user1, "PRJ-COST")).extracting("id").containsExactly(cost.getId());
    }

    @Test
    void approvalMovesDraftSubmittedApprovedThroughJpa() {
        TenantKey drafter = new TenantKey("COM1", "drafter");
        TenantKey manager = new TenantKey("COM1", "manager");

        ApprovalDocument draft = approvalService.createDraft(drafter, new ApprovalDocumentRequest(
                "expense",
                "approve",
                "EXPENSE",
                "{\"amount\":12000}",
                List.of("manager"),
                List.of(new ApprovalLineRequest(
                        ApprovalLineStage.PRIMARY,
                        1,
                        ApprovalRoleType.APPROVAL,
                        ApprovalDecisionMode.NORMAL,
                        ApprovalTargetType.USER,
                        "manager",
                        null,
                        null))));

        assertThat(draft.getStatus()).isEqualTo(ApprovalStatus.DRAFT);
        assertThat(draft.getDocumentJson()).isEqualTo("{\"amount\":12000}");
        assertThat(approvalService.listLines(drafter, draft.getId())).hasSize(1);

        ApprovalDocument submitted = approvalService.submit(drafter, draft.getId());
        assertThat(submitted.getStatus()).isEqualTo(ApprovalStatus.SUBMITTED);

        ApprovalDocument approved = approvalService.approve(manager, submitted.getId());

        assertThat(approved.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(approvalDocumentRepository.findById(approved.getId())).isPresent();
        assertThat(notificationService.countUnread(manager)).isEqualTo(1);
        assertThat(notificationService.countUnread(drafter)).isEqualTo(1);
    }

    @Test
    void approvalLineTemplateCanBeAppliedToDraft() {
        TenantKey drafter = new TenantKey("COM1", "drafter");
        ApprovalDocument draft = approvalService.createDraft(drafter, new ApprovalDocumentRequest(
                "expense",
                "approve",
                "EXPENSE",
                "{}",
                List.of(),
                List.of()));
        var template = approvalService.createTemplate(drafter, new ApprovalLineTemplateRequest(
                "basic",
                List.of(new ApprovalLineRequest(
                        ApprovalLineStage.PRIMARY,
                        1,
                        ApprovalRoleType.APPROVAL,
                        ApprovalDecisionMode.NORMAL,
                        ApprovalTargetType.USER,
                        "manager",
                        null,
                        null))));

        var lines = approvalService.applyTemplate(drafter, draft.getId(), template.getId());

        assertThat(lines).extracting("targetUserId").contains("manager");
    }

    @Test
    void consultReceiverCanResetConsultLines() {
        TenantKey drafter = new TenantKey("COM1", "drafter");
        ApprovalDocument draft = approvalService.createDraft(drafter, new ApprovalDocumentRequest(
                "consult",
                "approve",
                "CONSULT",
                "{}",
                List.of("manager"),
                List.of(
                        new ApprovalLineRequest(
                                ApprovalLineStage.PRIMARY,
                                1,
                                ApprovalRoleType.APPROVAL,
                                ApprovalDecisionMode.NORMAL,
                                ApprovalTargetType.USER,
                                "manager",
                                null,
                                null),
                        new ApprovalLineRequest(
                                ApprovalLineStage.PRIMARY,
                                1,
                                ApprovalRoleType.CONSULT,
                                ApprovalDecisionMode.NORMAL,
                                ApprovalTargetType.USER,
                                "consult1",
                                null,
                                null))));
        approvalService.submit(drafter, draft.getId());

        var reset = approvalService.resetConsultLines(new TenantKey("COM1", "consult1"), draft.getId(), List.of(
                new ApprovalLineRequest(
                        ApprovalLineStage.PRIMARY,
                        1,
                        ApprovalRoleType.CONSULT,
                        ApprovalDecisionMode.NORMAL,
                        ApprovalTargetType.USER,
                        "consult2",
                        null,
                        null)));

        assertThat(reset).extracting("targetUserId").containsExactly("consult2");
    }

    @Test
    void nonApproverCannotApprove() {
        TenantKey drafter = new TenantKey("COM1", "drafter");
        ApprovalDocument draft = approvalService.createDraft(drafter, new ApprovalDocumentRequest(
                "expense",
                "approve",
                List.of("manager")));
        approvalService.submit(drafter, draft.getId());

        assertThatThrownBy(() -> approvalService.approve(new TenantKey("COM1", "other"), draft.getId()))
                .isInstanceOf(GroupwareException.class);
    }
}
