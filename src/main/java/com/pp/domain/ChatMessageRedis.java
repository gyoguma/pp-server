package com.pp.domain;


import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Setter
@Getter
@RedisHash("chatmessages") // Specify the Redis key prefix
public class ChatMessageRedis {

    @Id
    private String id;

    private Long roomId;
    private Long sender;
    private String message;
    private String type;
    private LocalDateTime timestamp;

    public ChatMessageRedis() {
        this.id = (java.util.UUID.randomUUID().toString());
    } //generate unique ID
}