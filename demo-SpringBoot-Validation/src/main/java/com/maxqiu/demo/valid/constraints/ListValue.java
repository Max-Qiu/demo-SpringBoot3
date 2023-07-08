package com.maxqiu.demo.valid.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.maxqiu.demo.valid.constraintvalidators.ListValueValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 值在指定的List中
 *
 * @author Max_Qiu
 */
@Documented
@Constraint(validatedBy = {ListValueValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {
    String message() default "{com.maxqiu.demo.valid.constraints.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] valueList() default {};
}
