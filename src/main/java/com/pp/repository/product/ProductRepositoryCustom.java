package com.pp.repository.product;

import com.pp.web.dto.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductRepositoryCustom {

    Page<ProductResponseDTO.ShowProductDTO> getSearchProductList(String keyword, Pageable pageable);
}
