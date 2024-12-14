package com.pp.web.dto.product;

import com.pp.validation.annotation.ExistCategory;
import com.pp.validation.annotation.ExistLocation;
import com.pp.validation.annotation.ExistMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ProductRequestDTO {

    @Getter
    @Setter
    public static class RegistProductDTO {

        @ExistMember
        Long memberId;

        @ExistCategory
        Long categoryId;

        @NotBlank
        String title;

        @NotBlank
        String description;

        @NotNull
        Integer price;

        @ExistLocation
        Long locationId;
    }

    @Getter
    @Setter
    public static class EditProductDTO {

        @ExistMember
        Long memberId;

        @ExistCategory
        Long categoryId;

        @NotBlank
        String title;

        @NotNull
        String description;

        @NotNull
        Integer price;

        @ExistLocation
        Long locationId;
    }

    @Getter
    @Setter
    public static class SearchProductDTO {
        @NotBlank
        String keyword;
    }

}
