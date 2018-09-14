package common.filter;

import java.util.Locale;
import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import common.BaseContext;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import service.TranslationService;

public class ModelAction
    extends
        Action<WithModel>
{

	// --- STATIC FIELDS --- //

	public static final String	AUTHORIZATION_KEY_NAME	= "auth";
	public static final String	TRANSLATOR_KEY_NAME		= "translator";
	public static final String	ENTITY_KEY_NAME			= "entity";
	public static final String	TYPE_KEY_NAME			= "entity-type";
	public static final String	CONTEXT_KEY_NAME		= "context";

	// --- FIELDS --- //

	private final TranslationService translators;

	// --- CONSTRUCTORS --- //

	@Inject
	public ModelAction(
	                   TranslationService translators)
	{
		this.translators = translators;
	}

	// --- METHODS --- //

	@Override
	public CompletionStage<Result> call(
	    Http.Context ctx)
	{
		Class<?> cls = configuration.value();
		ctx.args.put(TYPE_KEY_NAME, cls);

		JsonNode body = ctx.request()
		                   .body()
		                   .asJson();
		if (body != null)
		{
			Object obj = Json.fromJson(body, cls);
			ctx.args.put(ENTITY_KEY_NAME, obj);
		}

		String localeName = ctx.request()
		                       .getQueryString("lang");
		if (localeName == null)
		{
			Cookie c = ctx.request()
			              .cookies()
			              .get("lang");
			if (c != null)
			{
				localeName = c.value();
			}
		}

		Locale l = Locale.ENGLISH;

		if (localeName != null)
		{
			String[] langAndCountry = localeName.split("_");
			if (langAndCountry.length == 1)
			{
				l = new Locale(langAndCountry[0]);
			} else if (langAndCountry.length == 2)
			{
				l = new Locale(langAndCountry[0], langAndCountry[1]);
			}
		}

		@SuppressWarnings(
			{
			        "rawtypes", "unchecked"
			})
		BaseContext context = new BaseContext(null, translators.forLocale(l));
		ctx.args.put(CONTEXT_KEY_NAME, context);

		return delegate.call(ctx);
	}

}
