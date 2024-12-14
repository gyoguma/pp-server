package com.pp.validation.validator;

import com.pp.apipayload.code.status.ErrorStatus;
import com.pp.repository.location.LocationRepository;
import com.pp.validation.annotation.ExistLocation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationExistValidator implements ConstraintValidator<ExistLocation, Long> {

    private final LocationRepository locationRepository;

    @Override
    public void initialize(ExistLocation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        // 거래 위치 id가 null이거나, 레포지토리에 존재하지 않으면 에러
        if (value == null || !locationRepository.existsById(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.LOCATION_NOT_FOUND.toString())
                    .addConstraintViolation();
            return false;
        }

        return true; // 검증 성공
    }
}