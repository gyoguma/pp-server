package com.pp.repository.chat;


import com.pp.web.dto.chat.ChatDTO;
import com.pp.web.dto.chat.ChatRoomDto;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ChatRoomRepository {


    private static final String CHAT_ROOM_KEY = "transaction";
    private static final String BUYER_INDEX_KEY = "buyer";
    private static final String SELLER_INDEX_KEY = "seller";
    private static final String CHAT_MESSAGE_KEY = "chat_message";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private HashOperations<String, String, Object> hashOperations;
    private SetOperations<String, String> setOperations;

    public ChatRoomRepository(RedisTemplate<String, Object> redisTemplate, RedisTemplate<String, String> stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash(); // Hash operations for Object values
        setOperations = stringRedisTemplate.opsForSet(); // Set operations for String values
    }


// In ChatRoomRepository.java

    public void addChatMessage(String roomId, ChatDTO message) {
        String messageId = UUID.randomUUID().toString();
        String messageKey = CHAT_MESSAGE_KEY + ":" + roomId + ":" + messageId; // Use roomId in key

        Map<String, Object> messageHash = new HashMap<>();
        messageHash.put("senderId", message.getSenderId());
        messageHash.put("nickname", message.getNickname());
        messageHash.put("message", message.getMessage());
        messageHash.put("timestamp", message.getTimestamp());
        messageHash.put("type", message.getType().name()); // Store enum as string

        hashOperations.putAll(messageKey, messageHash);
        setOperations.add(CHAT_MESSAGE_KEY + ":" + roomId, messageKey); // Add to room's message set

    }


    public void addChatRoom(ChatRoomDto chatRoom) {
        String roomId = CHAT_ROOM_KEY + ":" + chatRoom.getRoomId();

        // Store chat room data in a Hash
        hashOperations.putAll(roomId, chatRoomToHash(chatRoom));

        // Add to buyer and seller indexes
        setOperations.add(BUYER_INDEX_KEY + ":" + chatRoom.getBuyer(), roomId);
        setOperations.add(SELLER_INDEX_KEY + ":" + chatRoom.getSeller(), roomId);
    }
    public List<ChatDTO> findMessagesByRoomId(String roomId) {
        String messagesKey = CHAT_MESSAGE_KEY + ":" + roomId;
        Set<String> messageKeys = setOperations.members(messagesKey);
        List<ChatDTO> messages = new ArrayList<>();

        if (messageKeys != null) {
            for (String messageKey : messageKeys) {
                Map<Object, Object> messageHash = redisTemplate.opsForHash().entries(messageKey);
                if (!messageHash.isEmpty()) {
                    ChatDTO message = ChatDTO.builder()
                            .roomId(roomId)
                            .senderId(((String) messageHash.get("senderId")))
                            .nickname((String) messageHash.get("nickname"))
                            .message((String) messageHash.get("message"))
                            .timestamp((String) messageHash.get("timestamp"))
                            .type(ChatDTO.MessageType.valueOf((String) messageHash.get("type")))
                            .build();
                    messages.add(message);
                }
            }
        }

        // Sort messages by timestamp
        messages.sort((m1, m2) -> {
            LocalDateTime time1 = LocalDateTime.parse(m1.getTimestamp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime time2 = LocalDateTime.parse(m2.getTimestamp(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return time1.compareTo(time2);
        });

        return messages;
    }


    public List<ChatRoomDto> findChatRoomsByBuyer(String buyerId) {
        return findChatRoomsByIndex(BUYER_INDEX_KEY + ":" + buyerId);
    }

    public List<ChatRoomDto> findChatRoomsBySeller(String sellerId) {
        return findChatRoomsByIndex(SELLER_INDEX_KEY + ":" + sellerId);
    }


    // Updated method to find chat rooms by user (either buyer or seller)
    public List<ChatRoomDto> findChatRoomsByUser(String userId) {
        List<String> roomIdsByBuyer = new ArrayList<>(setOperations.members(BUYER_INDEX_KEY + ":" + userId));
        List<String> roomIdsBySeller = new ArrayList<>(setOperations.members(SELLER_INDEX_KEY + ":" + userId));

        // Combine the room IDs from both buyer and seller indexes
        List<String> allRoomIds = new ArrayList<>(roomIdsByBuyer);
        allRoomIds.addAll(roomIdsBySeller);

        // Fetch the chat room details for the combined room IDs
        List<ChatRoomDto> chatRooms = new ArrayList<>();
        for (String roomId : allRoomIds) {
            Map<String, Object> roomHash = hashOperations.entries(roomId);
            chatRooms.add(hashToChatRoomDto(roomHash, roomId));
        }

        return chatRooms.stream().distinct().collect(Collectors.toList());
    }

    public boolean findChatRoomByChatRoomId(String chatRoomId) {
        String roomId = CHAT_ROOM_KEY + ":" + chatRoomId;
        Map<String, Object> roomHash = hashOperations.entries(roomId);

        if (roomHash == null || roomHash.isEmpty()) {
            return false; // No chat room found for this ID
        }

        return true;
    }

    // Delete a chat room
    public void deleteChatRoom(String roomId) {
        String fullRoomId = CHAT_ROOM_KEY + ":" + roomId;

        // Get the chat room data before deleting to update indexes
        Map<String, Object> roomHash = hashOperations.entries(fullRoomId);
        if (roomHash != null && !roomHash.isEmpty()) {
            ChatRoomDto chatRoom = hashToChatRoomDto(roomHash, roomId);

            // Remove the room ID from the buyer and seller indexes
            setOperations.remove(BUYER_INDEX_KEY + ":" + chatRoom.getBuyer(), fullRoomId);
            setOperations.remove(SELLER_INDEX_KEY + ":" + chatRoom.getSeller(), fullRoomId);
            // Get all message keys for the room
            String messagesKey = CHAT_MESSAGE_KEY + ":" + roomId;
            Set<String> messageKeys = setOperations.members(messagesKey);

            // Delete all individual messages
            if (messageKeys != null) {
                redisTemplate.delete(messageKeys);
            }

            // Delete the set of message keys
            redisTemplate.delete(messagesKey);

            // Delete the chat room itself
            redisTemplate.delete(fullRoomId);
        }
    }

    private List<ChatRoomDto> findChatRoomsByIndex(String indexKey) {
        List<String> roomIds = new ArrayList<>(setOperations.members(indexKey));
        List<ChatRoomDto> chatRooms = new ArrayList<>();
        for (String roomId : roomIds) {
            Map<String, Object> roomHash = hashOperations.entries(roomId);
            chatRooms.add(hashToChatRoomDto(roomHash, roomId));
        }
        return chatRooms;
    }


    private Map<String, Object> chatRoomToHash(ChatRoomDto chatRoom) {
        Map<String, Object> hash = new HashMap<>();
        hash.put("roomId", chatRoom.getRoomId());
        hash.put("buyer", chatRoom.getBuyer());
        hash.put("seller", chatRoom.getSeller());
        hash.put("product", chatRoom.getProduct());
        hash.put("message", chatRoom.getMessage());
        hash.put("senderid", chatRoom.getSenderId());
        hash.put("time", chatRoom.getTime());
        return hash;
    }

    private ChatRoomDto hashToChatRoomDto(Map<String, Object> hash, String roomId) {
        return new ChatRoomDto(
                roomId,
                ((String) hash.get("buyer")),
                ((String) hash.get("seller")),
                ((String) hash.get("product")),
                (String) hash.get("message"),
                ((String) hash.get("senderid")),
                (String) hash.get("time")
        );
    }
}