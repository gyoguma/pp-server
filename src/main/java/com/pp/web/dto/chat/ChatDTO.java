package com.pp.web.dto.chat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter

public class ChatDTO {
    public enum MessageType{
        ENTER, TALK, LEAVE;
    }

    private String roomId; // 방 번호
    private String senderId; // 채팅을 보낸 사람
    private String nickname;
    private String message; // 메시지
    private String timestamp; // 채팅 발송 시간간
    private MessageType type; // 메시지 타입

    public ChatDTO(String roomId, String senderId, String nickname, String message, String timestamp, MessageType type) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }
}
