package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
