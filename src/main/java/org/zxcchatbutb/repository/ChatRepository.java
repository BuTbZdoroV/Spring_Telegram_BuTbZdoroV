package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.AbstractChat;
import org.zxcchatbutb.model.chat.ChatMember;

import java.util.List;
import java.util.Set;

public interface ChatRepository extends JpaRepository<AbstractChat, Long> {
    List<AbstractChat> findByMembers(Set<ChatMember> members);
}
