package com.pp.web.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ChatRoomRequestDTO {

    @Getter
    @Setter
    public static class SelectedTimeRequestDTO {

        @NotNull
        Long chatRoomId;

        @NotBlank
        String selectedTime;
    }

}
