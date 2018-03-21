//package com.sunit.global.base.authorization.validate.constraints;
//
//import java.lang.annotation.Documented;
//import static java.lang.annotation.ElementType.FIELD;
//import static java.lang.annotation.ElementType.METHOD;
//import static java.lang.annotation.ElementType.TYPE;
//import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
//import static java.lang.annotation.ElementType.CONSTRUCTOR;
//import static java.lang.annotation.ElementType.PARAMETER;
//import java.lang.annotation.Retention;
//import static java.lang.annotation.RetentionPolicy.RUNTIME;
//import java.lang.annotation.Target;
//import javax.validation.Constraint;
//import javax.validation.Payload;
//
//import org.hibernate.validator.constraints.impl.LengthValidator;
//
///** 
// * Validate that the string is between min and max included.
// *
// * @author Emmanuel Bernard
// * @author Hardy Ferentschik
// */
//@Documented
//@Constraint(validatedBy = LengthValidator.class)
//@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
//@Retention(RUNTIME)
//public @interface Equals { 
//	String other() default ""; 
//
//	String message() default "{org.hibernate.validator.constraints.Length.message}";
//
//	Class<?>[] groups() default { };
//
//	Class<? extends Payload>[] payload() default { };
//}
