package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zxcchatbutb.model.chat.Chat;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;

import java.util.List;
import java.util.Set;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    
    // Найти все членства в чатах для конкретного пользователя
    List<ChatMember> findByPerson(Person person);
    
    // Найти все членства в чатах по ID пользователя
    List<ChatMember> findByPersonId(Long personId);
    
    // Альтернативный вариант с JOIN FETCH для избежания N+1 проблемы
    @Query("SELECT cm FROM ChatMember cm JOIN FETCH cm.chat WHERE cm.person.id = :personId")
    List<ChatMember> findByPersonIdWithChat(@Param("personId") Long personId);
    
    // Вариант с получением только чатов
    @Query("SELECT cm.chat FROM ChatMember cm WHERE cm.person.id = :personId")
    List<Chat> findChatsByPersonId(@Param("personId") Long personId);

    Set<ChatMember> findChatMemberByPerson(Person person);

    Boolean existsByPersonIdAndChatId(Long personId, Long chatId);

}