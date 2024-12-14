package com.pp.validation.validator;

import com.pp.apipayload.code.status.ErrorStatus;
import com.pp.repository.category.CategoryRepository;
import com.pp.validation.annotation.ExistCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryExistValidator implements ConstraintValidator<ExistCategory, Long> {

    private final CategoryRepository categoryRepository;

    @Override
    public void initialize(ExistCategory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        // 카테고리 id가 null이거나, 레포지토리에 존재하지 않으면 에러
        if (value == null || !categoryRepository.existsById(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.CATEGORY_NOT_FOUND.toString())
                    .addConstraintViolation();
            return false;
        }

        return true; // 검증 성공
    }
}