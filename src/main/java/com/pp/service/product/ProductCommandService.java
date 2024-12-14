package com.pp.service.product;

import com.pp.domain.Category;
import com.pp.domain.Location;
import com.pp.domain.Product;
import com.pp.web.dto.product.ProductRequestDTO;

public interface ProductCommandService {

    Product registProduct(Product product);

    void editProduct(ProductRequestDTO.EditProductDTO request, Product product, Category category, Location location);

    void deleteProduct(Product product);
}
