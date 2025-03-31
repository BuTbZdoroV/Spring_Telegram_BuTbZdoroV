package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.Chat;
import org.zxcchatbutb.model.user.Person;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
