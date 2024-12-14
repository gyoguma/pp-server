package com.pp.web.dto.product;

import com.pp.domain.enums.ProductStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResponseDTO {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageUploadResultDTO {
        private Long productId;
        private List<ImageInfoDTO> uploadedImages;

        @Getter
        @Builder
        public static class ImageInfoDTO {
            private Long imageId;
            private String originFileName;
            private String storedFileName;
            private Integer size;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDetailDTO {
        private Long productId;
        private String title;
        private Integer price;
        private String description;
        private ProductStatus status;
        private List<ImageInfoDTO> images;

        @Getter
        @Builder
        public static class ImageInfoDTO {
            private Long imageId;
            private String originFileName;
            private String storedFileName;
            private Integer size;
        }
    }




    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistProductResultDTO {
        Long productId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditProductResultDTO {
        Long productId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetProductResultDTO { // 단일 상품 응답 DTO
        String title;
        String nickname;
        Integer price;
        Long locationId;
        String description;
        ProductStatus status;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    public static class ShowProductDTO { // 전체 상품 응답 DTO
        String title;
        String nickname;
        Integer price;
        Long productId;
        Long categoryId;
        ProductStatus status;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        @QueryProjection
        public ShowProductDTO(String title, String nickname, Integer price, Long productId, Long categoryId, ProductStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.title = title;
            this.nickname = nickname;
            this.price = price;
            this.productId = productId;
            this.categoryId = categoryId;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetProductListDTO { // 전체 상품 응답 DTO
        List<ShowProductDTO> productList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchProductResultDTO { // 상품 검색 응답 DTO
        List<ShowProductDTO> productList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProductListDTO { // 특정 회원 등록 상품 응답 DTO
        List<ShowProductDTO> productList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }
}
