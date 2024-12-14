package com.pp.web.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;



@Builder
@Schema(description = "채팅방 DTO")
public class ChatRoomDto {
    @Schema(description = "방id")
    private String roomId;
    @Schema(description = "buyer")
    private String buyer;
    @Schema(description = "seller")
    private String seller;
    @Schema(description = "product")
    private String product;
    @Schema(description = "message")
    private String message;
    @Schema(description = "sender")
    private String senderId;
    private String time;


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatRoomDto() {
    }

    public ChatRoomDto(String roomId, String buyer, String seller, String product, String message, String senderId, String time) {
        this.roomId = roomId;
        this.buyer = buyer;
        this.seller = seller;
        this.product = product;
        this.message = message;
        this.senderId = senderId;
        this.time = time;
    }
}
