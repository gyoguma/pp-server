package com.pp.web.controller;


import com.pp.service.chat.ChatRoomService;
import com.pp.web.dto.chat.ChatDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final RedisTemplate redisTemplate;

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
//    @MessageMapping("/connect")
//    public void handleConnect(@Header("simpSessionId") String sessionId,
//                             SimpMessageHeaderAccessor headerAccessor) {
//        log.info("Received CONNECT frame from session: {}", sessionId);
//
//        // Send CONNECTED frame
//        messagingTemplate.convertAndSend("/user/" + sessionId + "/queue/connect",
//            "CONNECTED\n" +
//            "version:1.1\n" +
//            "heart-beat:0,0\n" +
//            "\n\u0000");
//    }



@EventListener
public void handleWebSocketConnectListener(SessionConnectEvent event) {
    log.info("Received a new web socket connection");
    StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
    
    // Send CONNECTED frame
    SimpMessageHeaderAccessor connectAck = SimpMessageHeaderAccessor.create(SimpMessageType.CONNECT_ACK);
    connectAck.setSessionId(sha.getSessionId());
    messagingTemplate.convertAndSendToUser(
        sha.getSessionId(),
        "/queue/connect",
        "CONNECTED\nversion:1.1\n\n\u0000",
        connectAck.getMessageHeaders()
    );
}




    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO) {
        log.info("sendMessage: {}", chatDTO);

        if (chatDTO.getTimestamp() == null) {
            chatDTO.setTimestamp(getCurrentTimestamp());
        }

        // Save message to Redis
        chatRoomService.sendMessage(chatDTO);

        // Send message to subscribers of the room
       // messagingTemplate.convertAndSend("/sub/chat/room/" + chatDTO.getRoomId(), chatDTO);
    }

    @MessageMapping("/chat/leaveUser")
    public void leaveUser(@Payload ChatDTO chatDTO) {
        log.info("leaveUser: {}", chatDTO);

        // Customize leave message (optional)
        chatDTO.setMessage(chatDTO.getNickname() + "님이 퇴장했습니다.");
        chatDTO.setType(ChatDTO.MessageType.LEAVE);
        chatDTO.setTimestamp(getCurrentTimestamp());

        // Save leave message (optional, if you want to store it)
        chatRoomService.sendMessage(chatDTO);

        // Notify subscribers
    }


    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDTO chatDTO) {
        log.info("enterUser: {}", chatDTO);

        // Check if this is a duplicate ENTER message
        String cacheKey = "enter:" + chatDTO.getRoomId() + ":" + chatDTO.getSenderId();
        if (redisTemplate.hasKey(cacheKey)) {
            log.info("Duplicate ENTER message detected, ignoring");
            return;
        }
        // Set a short-lived cache entry to prevent duplicates
        redisTemplate.opsForValue().set(cacheKey, "1", 5, TimeUnit.SECONDS);



        chatDTO.setMessage(chatDTO.getNickname() + "님이 입장했습니다.");
        chatDTO.setType(ChatDTO.MessageType.ENTER);
        chatDTO.setTimestamp(getCurrentTimestamp());

        // Save enter message (optional, if you want to store it)
        chatRoomService.sendMessage(chatDTO);

        // Notify subscribers

    }


}
