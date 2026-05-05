package com.upmudoum.groupware.domain.message.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.message.entity.MessageAttachment;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, UUID> {

    List<MessageAttachment> findByComCdAndMessageIdAndDeletedYnFalseOrderByUploadedAtAsc(String comCd, UUID messageId);

    Optional<MessageAttachment> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);
}
