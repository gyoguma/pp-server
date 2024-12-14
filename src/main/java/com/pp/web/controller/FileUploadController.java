package com.pp.web.controller;

import com.pp.apipayload.ApiResponse;
import com.pp.converter.ProductConverter;
import com.pp.domain.Image;
import com.pp.domain.Product;
import com.pp.service.image.ImageCommandService;
import com.pp.service.product.ProductQueryService;
import com.pp.validation.annotation.ExistProduct;
import com.pp.web.dto.product.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class FileUploadController {


    @Autowired
    private final ImageCommandService imageCommandService;

    @Autowired
    private final ProductQueryService productQueryService;

    @PostMapping(value = "/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "상품 이미지 업로드", description = "특정 상품의 이미지를 업로드")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ApiResponse<ProductResponseDTO.ProductImageUploadResultDTO> uploadProductImages(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "업로드할 이미지 파일들", required = true)
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {

        List<Image> uploadedImages = imageCommandService.uploadProductImages(productId, images);

        return ApiResponse.onSuccess(
                ProductConverter.toProductImageUploadResultDTO(
                        uploadedImages.get(0).getProduct(), uploadedImages
                )
        );
    }

    @GetMapping(value = "/{productId}/images")
    @Operation(summary = "상품 이미지 가져오기", description = "특정 상품의 이미지를 업로드")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 가져오기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @Parameters({
            @Parameter(name = "productId", description = "상품id로 이미지를 받는 api")
    })
    public ApiResponse<ProductResponseDTO.ProductDetailDTO> getProductImages(@ExistProduct @PathVariable Long productId) throws IOException {
        Product product = productQueryService.getProduct(productId);
        return ApiResponse.onSuccess(ProductConverter.toProductDetailDTO(product));
    }



    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "상품 이미지 삭제 API", description = "특정 이미지를 삭제합니다.")
    public ApiResponse<Void> deleteProductImage(
            @PathVariable Long imageId
    ) {
        imageCommandService.deleteProductImages(imageId);
        return ApiResponse.onSuccess(null);
    }
}
