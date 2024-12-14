package com.pp.repository.chat;


import com.pp.domain.ChatRoomList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomListRepository extends JpaRepository<ChatRoomList, String> {
}