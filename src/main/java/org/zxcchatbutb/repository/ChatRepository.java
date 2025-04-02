package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.chat.Chat;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;

import java.util.List;
import java.util.Set;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByMembers(Set<ChatMember> members);
}
