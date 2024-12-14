package com.pp.web.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ReviewRequestDTO {

        @Getter
        @Setter
        public static class ReviewRatingDTO {

            @NotNull
            Double starRating;
        }
}
