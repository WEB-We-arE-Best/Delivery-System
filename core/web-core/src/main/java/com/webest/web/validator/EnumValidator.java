package com.webest.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumValidation, String> {

    private EnumValidation validation;

    @Override
    public void initialize(EnumValidation constraintAnnotation) {
        this.validation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext context) {
        if (input == null || input.isBlank()) {
            return true;
        }

        Enum<?>[] enumConstants = this.validation.target().getEnumConstants();
        
        boolean contains = Arrays.stream(enumConstants).map(Enum::name)
            .anyMatch(name -> name.equals(input));

        if (!contains) {
            String allowedValues = Arrays.stream(enumConstants)
                .map(Enum::name)
                .collect(Collectors.joining(", "));

            // 동적 메시지를 설정
            context.disableDefaultConstraintViolation();  // 기본 메시지 비활성화
            context.buildConstraintViolationWithTemplate(
                    String.format("입력값 '%s'은 올바르지 않습니다. 허용되는 값: [%s]", input, allowedValues))
                .addConstraintViolation();
        }

        return contains;
    }
}
