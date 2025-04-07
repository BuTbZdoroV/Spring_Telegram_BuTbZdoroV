package org.zxcchatbutb.model.chat;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PUBLIC")
@NoArgsConstructor
public class PublicChat extends AbstractChat {
    @Override
    public ChatType getChatType() {
        return ChatType.PUBLIC;
    }


}
