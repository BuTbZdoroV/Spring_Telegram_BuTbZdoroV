package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.Attachment;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByMessageId(Long messageId);
}
