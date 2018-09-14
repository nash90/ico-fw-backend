package controllers;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import common.BaseContext;
import common.Translator;
import common.filter.ModelAction;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Http.Request;
import service.AuthorizationService;
import service.TranslationService;
import service.UserService;
import serviceImpl.AuthorizationServiceImpl;

public class BaseController
    extends
        Controller
{
	private static final String	ACCESS_TOKEN	= "token";
	private static final String	HTTP_HMAC		= "HMAC";

	@Inject
	private TranslationService	ts;
	@Inject
	private UserService			userService;

	protected JsonNode getBody()
	{
		return request().body()
		                .asJson();
	}

	protected Map<String, String[]> getFormBody()
	{
		return request().body()
		                .asFormUrlEncoded();
	}

	/**
	 * Annotate your method with WithModel when calling this.
	 * 
	 * @return The body parsed as an Entity
	 */
	@SuppressWarnings("unchecked")
	protected <T> T entity()
	{
		return (T) ctx().args.get(ModelAction.ENTITY_KEY_NAME);
	}

	protected Translator getTranslator()
	{
		Translator t = (Translator) ctx().args.get(ModelAction.TRANSLATOR_KEY_NAME);
		if (t == null)
		{
			Locale l = getLocale(request());
			t = ts.forLocale(l);
		}
		return t;
	}

	public static Locale getLocale(
	    Request r)
	{
		assert r != null;
		String localeName = r.getQueryString("lang");
		if (localeName == null)
		{
			Cookie c = r.cookies()
			            .get("lang_code");
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

		return l;
	}

	protected AuthorizationService getAuth()
	{
		AuthorizationService auth = new AuthorizationServiceImpl(userService);
		auth.initAuthorization(request());
		return auth;
	}

	protected BaseContext getContext()
	{
		return new BaseContext(getAuth(), getTranslator());
	}

	protected String getToken()
	{
		String token = null;
		Cookie cookie = request().cookies()
		                         .get(ACCESS_TOKEN);
		if (cookie != null)
		{
			token = cookie.value();
		}
		return token;
	}

	protected String getHmacHeader()
	{
		Optional<String> hmac = null;
		Request r = request();
		hmac = r.getHeaders()
		        .get(HTTP_HMAC);
		return hmac.get();
	}
}
