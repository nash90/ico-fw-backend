package common.filter;

import java.lang.annotation.*;
import play.mvc.With;

@With(ModelAction.class)
@Target(
	{
	        ElementType.TYPE, ElementType.METHOD
	})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithModel
{

	public Class<?> value();

}
