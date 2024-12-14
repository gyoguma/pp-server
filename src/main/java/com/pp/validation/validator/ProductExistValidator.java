package com.pp.validation.validator;

import com.pp.apipayload.code.status.ErrorStatus;
import com.pp.repository.product.ProductRepository;
import com.pp.validation.annotation.ExistProduct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductExistValidator implements ConstraintValidator<ExistProduct, Long> {

    private final ProductRepository productRepository;

    @Override
    public void initialize(ExistProduct constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        // 상품 id가 null이거나, 레포지토리에 존재하지 않으면 에러
        if (value == null || !productRepository.existsById(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.PRODUCT_NOT_FOUND.toString())
                    .addConstraintViolation();
            return false;
        }

        return true; // 검증 성공
    }
}