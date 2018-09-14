package common.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import play.mvc.With;

@With(ValidationAction.class)
@Target(
	{
	        ElementType.TYPE, ElementType.METHOD
	})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithValidation
{
	boolean selective() default false;
}
