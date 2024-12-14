package com.pp.validation.validator;

import com.pp.apipayload.code.status.ErrorStatus;
import com.pp.validation.annotation.CheckPage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;


@Component
public class PageCheckValidator implements ConstraintValidator<CheckPage, Integer> {

    @Override
    public void initialize(CheckPage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        // 요청으로 온 페이지 번호가 null이거나 1 미만이면 검증 실패
        if (value == null || value < 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.PAGE_NOT_FOUND.toString())
                    .addConstraintViolation();
            return false;
        }

        return true; // 검증 성공
    }
}
