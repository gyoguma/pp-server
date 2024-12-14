package com.pp.web.controller;


import com.pp.config.auth.TokenService;
import com.pp.service.chat.ChatRoomService;
import com.pp.web.dto.chat.ChatDTO;
import com.pp.web.dto.chat.ChatRoomDto;
import com.pp.web.dto.chat.UserChatRoomMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController // Add this annotation
@RequiredArgsConstructor
public class ChatRoomController {

    private final TokenService tokenService;
    private final UserChatRoomMap userChatRoomMap;
    private final ChatRoomService chatRoomService;

    //방만들기
    @PostMapping("/chat")
    @Operation(
            summary = "채팅방 생성",
            description = "새로운 채팅방을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채팅방 정보를 포함한 요청 본문",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChatRoomDto.class)
                    )
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ResponseEntity<?> createRoom(@RequestBody ChatRoomDto chatRoomDto) {
        try {
            log.info("Creating room. Buyer: {}, Seller: {}, Product: {}, senderId: {}",
                    chatRoomDto.getBuyer(), chatRoomDto.getSeller(), chatRoomDto.getProduct(), chatRoomDto.getSenderId());



            
            String combined = chatRoomDto.getBuyer() + "-" + chatRoomDto.getSeller() + "-" + chatRoomDto.getProduct();
            //해시바꾸기
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            // Base64 URL-safe 인코딩
            String customRoomId = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            log.info("customRoomId: {}", customRoomId);




            ChatRoomDto newRoom = new ChatRoomDto();
            newRoom.setRoomId(customRoomId);
            newRoom.setBuyer(chatRoomDto.getBuyer());
            newRoom.setSeller(chatRoomDto.getSeller());
            newRoom.setProduct(chatRoomDto.getProduct());
            newRoom.setSenderId(chatRoomDto.getSenderId());
            chatRoomService.addChatRoom(newRoom);

            log.info("Received parameters chatRoomDto: {}", chatRoomDto);
            return ResponseEntity.ok(newRoom);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating chat room", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create chat room");
        }
    }

    @GetMapping("/chat/buyer/{buyerId}")
    @Parameters({
            @Parameter(name = "buyerId", description = "buyerId")
    })
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsByBuyer(@PathVariable String buyerId) {
        List<ChatRoomDto> chatRooms = userChatRoomMap.findChatRoomsByBuyer(buyerId);
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    @GetMapping("/chat/seller/{sellerId}")
    @Parameters({
            @Parameter(name = "sellerId", description = "sellerId")
    })
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsBySeller(@PathVariable String sellerId) {
        List<ChatRoomDto> chatRooms = userChatRoomMap.findChatRoomsBySeller(sellerId);
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    // Get chat rooms for a user (either buyer or seller)
    @GetMapping("/chat/user/{userId}")
    @Parameters({
            @Parameter(name = "userId", description = "userId")
    })
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsByUser(@PathVariable String userId) {
        log.info("Fetching chat rooms for user: {}", userId);
        List<ChatRoomDto> chatRooms = userChatRoomMap.findChatRoomsByUser(userId);
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }


    // Delete a chat room
    @DeleteMapping("/chat/{roomId}")
    @Parameters({
            @Parameter(name = "roomId", description = "roomId")
    })
    public ResponseEntity<String> deleteChatRoom(@PathVariable String roomId) {
        log.info("Deleting chat room: {}", roomId);
        userChatRoomMap.deleteChatRoom(roomId);
        return new ResponseEntity<>("Chat room deleted successfully", HttpStatus.OK);
    }

    // In ChatController.java
    @PostMapping("/chat/{roomId}/{userId}")
    @Parameters({
            @Parameter(name = "roomId", description = "roomId"),
            @Parameter(name = "userId", description = "userId")
    })
    public ResponseEntity<String> enterChatRoom(@PathVariable String roomId, @PathVariable String userId) {
        log.info("방 들어옴 User {} entering room {}", userId, roomId);
        String message = userChatRoomMap.enterChatRoom(roomId, userId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/chat/{roomId}")
    @Parameters({
            @Parameter(name = "roomId", description = "roomId")
    })
    public ResponseEntity<List<ChatDTO>> getChatHistory(@PathVariable String roomId) {
        log.info("Getting chat history for room: {}", roomId);

        try {
            List<ChatDTO> chatHistory = chatRoomService.findMessagesByRoomId(roomId);
            log.info("Found {} messages for room {}", chatHistory.size(), roomId);

            if (chatHistory.isEmpty()) {
                log.info("No messages found for room {}", roomId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(chatHistory, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching chat history for room {}: {}", roomId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


