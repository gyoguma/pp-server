package com.pp.repository.chat;


import com.pp.domain.ChatMessageRedis;
import com.pp.web.dto.chat.ChatRoomDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisChatMessageRepository extends CrudRepository<ChatMessageRedis, String> {
    List<ChatMessageRedis> findByRoomIdOrderByTimestampAsc(Long roomId);
    void deleteByRoomId(Long roomId);
    void saveUserRooms(String sellerId, List<ChatRoomDto> rooms);
    List<ChatRoomDto> getUserRooms(String sellerId);
    List<ChatRoomDto> getAllRooms();


}