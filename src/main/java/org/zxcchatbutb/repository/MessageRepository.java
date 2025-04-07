package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);
}
