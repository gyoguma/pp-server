package com.pp.config.chat;

import com.pp.web.dto.chat.ChatRoomMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ChatRoomConfig {

    @Bean
    public Map<Long, ChatRoomMap> userChatRooms() {
        return new ConcurrentHashMap<>();
    }
}