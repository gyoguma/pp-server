package com.pp.validation.validator;

import com.pp.apipayload.code.status.ErrorStatus;
import com.pp.repository.member.MemberRepository;
import com.pp.validation.annotation.ExistMember;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberExistValidator implements ConstraintValidator<ExistMember, Long> {

    private final MemberRepository memberRepository;

    @Override
    public void initialize(ExistMember constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        // 회원 id가 null이거나, 레포지토리에 존재하지 않으면 에러
        if (value == null || !memberRepository.existsById(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.MEMBER_NOT_FOUND.toString())
                    .addConstraintViolation();
            return false;
        }

        return true; // 검증 성공
    }
}