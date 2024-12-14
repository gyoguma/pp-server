package com.pp.web.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ChatRoomResponseDTO {

    @Getter
    @Setter
    public static class SelectedTimeResponseDTO {

        @NotNull
        Long chatRoomId;

        @NotBlank
        String selectedTime;
    }
}
