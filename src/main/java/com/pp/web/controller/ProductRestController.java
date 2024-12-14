package com.pp.web.controller;


import com.pp.apipayload.ApiResponse;
import com.pp.converter.ProductConverter;
import com.pp.domain.Category;
import com.pp.domain.Location;
import com.pp.domain.Member;
import com.pp.domain.Product;
import com.pp.service.category.CategoryQueryService;
import com.pp.service.location.LocationQueryService;
import com.pp.service.member.MemberQueryService;
import com.pp.service.product.ProductCommandService;
import com.pp.service.product.ProductQueryService;
import com.pp.validation.annotation.CheckPage;
import com.pp.validation.annotation.ExistMember;
import com.pp.validation.annotation.ExistProduct;
import com.pp.web.dto.product.ProductRequestDTO;
import com.pp.web.dto.product.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Validated
public class ProductRestController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    private final MemberQueryService memberQueryService;
    private final CategoryQueryService categoryQueryService;
    private final LocationQueryService locationQueryService;

    @PostMapping("/")
    @Operation(summary = "상품 등록 API",description = "상품을 등록하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<ProductResponseDTO.RegistProductResultDTO> register(@RequestBody @Valid ProductRequestDTO.RegistProductDTO request){
        Member member = memberQueryService.getMember(request.getMemberId());
        Category category = categoryQueryService.getCategory(request.getCategoryId());
        Location location = locationQueryService.getLocation(request.getLocationId());
        Product newProduct = ProductConverter.toRegistProduct(request, member, category, location);
        Product product = productCommandService.registProduct(newProduct);
        return ApiResponse.onSuccess(ProductConverter.toRegistProductResultDTO(product));
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "상품 수정 API", description = "상품을 수정하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "productId", description = "수정할 상품의 아이디, path variable 입니다.")
    })
    public ApiResponse<ProductResponseDTO.EditProductResultDTO> edit(@ExistProduct @PathVariable Long productId,
                                                                     @RequestBody @Valid ProductRequestDTO.EditProductDTO request) {
        Product product = productQueryService.getProduct(productId);
        Category category = categoryQueryService.getCategory(request.getCategoryId());
        Location location = locationQueryService.getLocation(request.getLocationId());
        productCommandService.editProduct(request, product, category, location);
        return ApiResponse.onSuccess(ProductConverter.toEditProductResultDTO(product));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제 API", description = "상품을 삭제하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "productId", description = "삭제할 상품의 아이디, path variable 입니다.")
    })
    public ApiResponse<Void> delete(@ExistProduct @PathVariable Long productId) {
        Product product = productQueryService.getProduct(productId);
        productCommandService.deleteProduct(product);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "단일 상품 조회 API", description = "단일 상품을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "productId", description = "조회할 상품의 아이디, path variable 입니다.")
    })
    public ApiResponse<ProductResponseDTO.GetProductResultDTO> getProduct(@ExistProduct @PathVariable Long productId) {
        Product product = productQueryService.getProduct(productId);
        return ApiResponse.onSuccess(ProductConverter.toProductResultDTO(product));
    }

    @GetMapping("/")
    @Operation(summary = "전체 상품 조회 API", description = "전체 상품을 조회하는 API이며, 페이징을 포함합니다. query String 으로 page 번호를 주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호, query string 입니다. 1 이상의 페이지 번호를 보내주세요.")
    })
    public ApiResponse<ProductResponseDTO.GetProductListDTO> getProductList(@CheckPage @RequestParam Integer page) {
        Page<Product> productPage = productQueryService.getProductList(page - 1);
        return ApiResponse.onSuccess(ProductConverter.toProductListDTO(productPage));
    }

    @GetMapping("/search")
    @Operation(summary = "키워드 상품 검색 API",description = "키워드로 상품을 검색하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "keyword", description = "키워드 값, query string 입니다. 검색하고 싶은 키워드를 보내주세요."),
            @Parameter(name = "page", description = "페이지 번호, query string 입니다. 1 이상의 페이지 번호를 보내주세요.")
    })
    public ApiResponse<ProductResponseDTO.SearchProductResultDTO> search(@RequestParam @Valid String keyword,
                                                                         @CheckPage @RequestParam Integer page){
        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<ProductResponseDTO.ShowProductDTO> searchProductPage = productQueryService.searchProducts(keyword, pageable);
        return ApiResponse.onSuccess(ProductConverter.toSearchProductListDTO(searchProductPage));
    }

    @GetMapping("/members/{memberId}")
    @Operation(summary = "특정 회원이 등록한 상품 조회 API", description = "특정 회원이 등록한 상품을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "회원의 아이디, path variable 입니다!")
    })
    public ApiResponse<ProductResponseDTO.MemberProductListDTO> getMember(@ExistMember @PathVariable Long memberId, @CheckPage @RequestParam Integer page) {
        Member member = memberQueryService.getMember(memberId);
        Page<Product> productPage = memberQueryService.getMemberProductList(member, page - 1);
        return ApiResponse.onSuccess(ProductConverter.toMemberProductListDTO(productPage));
    }
}
