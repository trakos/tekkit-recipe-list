package pl.trakos.TekkitRecipeList.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompositeKey {
    String key1();
    String key2() default "";
    String key3() default "";
    String key4() default "";
}
