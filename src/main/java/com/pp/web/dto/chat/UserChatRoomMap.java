package com.pp.web.dto.chat;

import com.pp.repository.chat.ChatRoomRepository;
import com.pp.service.chat.ChatRoomService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//유저별 채팅방 관리
@Component
public class UserChatRoomMap {
    private final Map<String, ChatRoomMap> userChatRooms = new ConcurrentHashMap<>();

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    public UserChatRoomMap(ChatRoomRepository chatRoomRepository, ChatRoomService chatRoomService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomService = chatRoomService;
    }

    public void addChatRoom(ChatRoomDto room) {
        chatRoomRepository.addChatRoom(room);
    }

    public List<ChatRoomDto> findChatRoomsByBuyer(String buyerId) {
        return chatRoomRepository.findChatRoomsByBuyer(buyerId);
    }

    public List<ChatRoomDto> findChatRoomsBySeller(String sellerId) {
        return chatRoomRepository.findChatRoomsBySeller(sellerId);
    }

    // Get chat rooms where the user is either the buyer or the seller
    public List<ChatRoomDto> findChatRoomsByUser(String userId) {
        return chatRoomRepository.findChatRoomsByUser(userId);
    }

    // Delete a chat room by ID
    public void deleteChatRoom(String roomId) {
        chatRoomRepository.deleteChatRoom(roomId);
    }

    public String enterChatRoom(String roomId, String userId) {
        try {
            // First check if the room exists in Redis
            List<ChatDTO> messages = chatRoomRepository.findMessagesByRoomId(roomId);
            if (messages == null || messages.isEmpty()) {
                throw new IllegalArgumentException("Chat room not found: " + roomId);
            }
            // Use the ChatRoomService to handle the enter room logic
            return chatRoomService.enterChatRoom(roomId, userId);
        } catch (IllegalArgumentException e) {
            throw e;  // Re-throw the exception to be handled by the controller
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter chat room: " + roomId, e);
        }
    }



}
