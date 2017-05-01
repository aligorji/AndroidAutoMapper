package ir.aligorji.automapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoMapperOption
{
    boolean ignore() default false;
    String from() default "";
    String prefix() default "";
    String postfix() default "";
}
