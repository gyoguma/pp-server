package com.pp.web.dto.chat;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 싱글톤으로 생성
// 모든 ChatService 에서 ChatRooms가 공통된 필요함으로
//하나의 채팅방 관리합
@Getter
@Setter
public class ChatRoomMap {
    private Map<String, ChatRoomDto> chatRooms = new LinkedHashMap<>();


    // 특정 채팅방 반환 메서드
    public ChatRoomDto getChatRoom(String roomId) {
        return chatRooms.get(roomId);
    }

    // 채팅방 맵 전체 반환
    public Map<String, ChatRoomDto> getChatRooms() {
        return chatRooms;
    }

    // 채팅방 값 목록 반환 메서드
    public List<ChatRoomDto> getChatRoomList() {
        return new ArrayList<>(chatRooms.values());
    }


    // 채팅방 삭제 메서드, 결과 반환
    public boolean removeChatRoom(String roomId) {
        return chatRooms.remove(roomId) != null;
    }
}
