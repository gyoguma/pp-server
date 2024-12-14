package com.pp.service.chat;


import com.pp.domain.ChatRoomList;
import com.pp.repository.chat.ChatRoomListRepository;
import com.pp.repository.chat.ChatRoomRepository;
import com.pp.web.dto.chat.ChatDTO;
import com.pp.web.dto.chat.ChatRoomDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomListRepository chatRoomListRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatRoomListRepository chatRoomListRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomListRepository = chatRoomListRepository;
        this.messagingTemplate = messagingTemplate;
    }


    public void sendMessage(ChatDTO message) {
        // Set timestamp if not provided
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // Save message to Redis
        chatRoomRepository.addChatMessage(message.getRoomId(), message);

        // Send message to subscribers of the room (real-time update)
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    public String enterChatRoom(String roomId, String userId) {
        // Verify if the chat room exists
        List<ChatDTO> messages = chatRoomRepository.findMessagesByRoomId(roomId);
        if (messages == null) {
            throw new IllegalArgumentException("Chat room not found: " + roomId);
        }
        
        log.info("방 들어옴");
        // Send a system message to the room
        ChatDTO systemMessage = ChatDTO.builder()
            .roomId(roomId)
            .senderId(userId)
            .type(ChatDTO.MessageType.ENTER)
            .message("사용자가 들어왔습니다")
            .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
        
        // Save and broadcast the system message
        sendMessage(systemMessage);

        return "Successfully entered chat room: " + roomId;
    }

    public void addChatRoom(ChatRoomDto chatRoomDto) {
        // Generate a new room ID if not set
        if (chatRoomDto.getRoomId() == null) {
            // You could use a sequence generator or UUID, here using system time as example
           log.error("no room id when creating cahat room");
            throw new IllegalArgumentException("no room Id");
        }
        
        // Validate required fields
        if (chatRoomDto.getBuyer() == null || chatRoomDto.getSeller() == null || chatRoomDto.getProduct() == null) {
            throw new IllegalArgumentException("Buyer, seller and product IDs are required");
        }
        
        // Set timestamp if not provided
        if (chatRoomDto.getTime() == null) {
            chatRoomDto.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }


        if(!chatRoomRepository.findChatRoomByChatRoomId(chatRoomDto.getRoomId())) {
            log.info("방없어서새로만듬");
            chatRoomRepository.addChatRoom(chatRoomDto);

            ChatRoomList roomList = ChatRoomList.builder()
                    .buyer(chatRoomDto.getBuyer())
                    .seller(chatRoomDto.getSeller())
                    .product(chatRoomDto.getProduct())
                    .roomId(chatRoomDto.getRoomId())
                    .build();
            chatRoomListRepository.save(roomList);
            log.info("db저장 성공!");

            // Send a system message to the room
            ChatDTO systemMessage = ChatDTO.builder()
                    .roomId(chatRoomDto.getRoomId())
                    .senderId(chatRoomDto.getSenderId())
                    .type(ChatDTO.MessageType.ENTER)
                    .message("Chat room created")
                    .timestamp(chatRoomDto.getTime())
                    .build();
            // Broadcast the system message
            sendMessage(systemMessage);
        }else {
            log.info("이미 방 잇음");
        }


    }

    public List<ChatRoomDto> findChatRoomsByBuyer(String buyerId) {
        return chatRoomRepository.findChatRoomsByBuyer(buyerId);
    }

    public List<ChatRoomDto> findChatRoomsBySeller(String sellerId) {
        return chatRoomRepository.findChatRoomsBySeller(sellerId);
    }

    public List<ChatRoomDto> findChatRoomsByUser(String userId) {
        return chatRoomRepository.findChatRoomsByUser(userId);
    }

    public void deleteChatRoom(String roomId) {
        chatRoomRepository.deleteChatRoom(roomId);
    }
    public List<ChatDTO> findMessagesByRoomId(String roomId) {
        log.info("Service: Finding messages for room {}", roomId);
        try {
            List<ChatDTO> messages = chatRoomRepository.findMessagesByRoomId(roomId);
            log.info("Service: Found {} messages", messages.size());
            return messages;
        } catch (Exception e) {
            log.error("Service: Error finding messages for room {}: {}", roomId, e.getMessage(), e);
            throw e;
        }
    }
}
