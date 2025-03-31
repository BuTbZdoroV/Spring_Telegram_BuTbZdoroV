package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
