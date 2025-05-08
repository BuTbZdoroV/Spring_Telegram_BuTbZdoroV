package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zxcchatbutb.model.chat.AbstractChat;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.chat.PrivateChat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRepository extends JpaRepository<AbstractChat, Long> {
    List<AbstractChat> findByMembers(Set<ChatMember> members);

    @Query("SELECT pc FROM PrivateChat pc " +
            "JOIN pc.members m1 " +
            "JOIN pc.members m2 " +
            "WHERE m1.person.id = :person1Id AND m2.person.id = :person2Id")
    Optional<PrivateChat> findChatBetweenUsers(@Param("person1Id") Long person1Id,
                                               @Param("person2Id") Long person2Id);
}
