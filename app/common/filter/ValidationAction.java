package common.filter;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import common.BaseContext;
import common.ErrorMessage;
import common.ErrorMessage.Code;
import common.ServerException;
import common.Translator;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class ValidationAction
    extends
        Action<WithValidation>
{
	// --- STATIC FIELDS --- //

	private final ValidatorFactory	factory		= Validation.buildDefaultValidatorFactory();
	public final Validator			validator	= factory.getValidator();

	// --- CONSTRUCTORS --- //

	public ValidationAction()
	{
		// Nothing
	}

	// --- METHODS --- //

	@Override
	public CompletionStage<Result> call(
	    Http.Context ctx)
	{
		Object obj = ctx.args.get(ModelAction.ENTITY_KEY_NAME);
		if (obj == null)
		{
			throw new ServerException("Model should be parsed before validation");
		}

		BaseContext context = (BaseContext) (ctx.args.get(ModelAction.CONTEXT_KEY_NAME));

		if (context == null)
		{
			throw new ServerException("This should not happen: context object is null");
		}

		ErrorMessage err = validate(configuration.selective(), context.translator(), obj);
		if (err != null)
		{

			return CompletableFuture.completedFuture(badRequest(err.toJson()).as(Http.MimeTypes.JSON));
		}

		return delegate.call(ctx);
	}

	public ErrorMessage validate(
	    boolean selective,
	    Translator tr,
	    Object obj)
	{
		Class<?> cls = obj.getClass();
		ErrorMessage result = null;
		for (Field f : cls.getDeclaredFields())
		{
			try
			{
				f.setAccessible(true);
				if ((!f.isSynthetic() && selective && f.get(obj) != null) || !selective)
				{
					Set<ConstraintViolation<Object>> violations = validator.validateProperty(obj, f.getName(), javax.validation.groups.Default.class);
					for (ConstraintViolation<Object> v : violations)
					{
						ErrorMessage err = new ErrorMessage(Code.INVALID_INPUT, tr.get(v.getMessage()), v.getPropertyPath()
						                                                                                 .toString());
						result = ErrorMessage.merge(result, err);
					}
				}
				f.setAccessible(false);
			} catch (IllegalAccessException | IllegalArgumentException e)
			{
				f.setAccessible(false);
				throw new Error("Unexpected error validating field " + f.getName(), e);
			}
		}
		return result;
	}

}
