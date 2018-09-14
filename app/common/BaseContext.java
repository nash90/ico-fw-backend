package common;

import service.AuthorizationService;

public class BaseContext
{
	private final AuthorizationService	auth;
	private final Translator			translator;

	public BaseContext(
	                   AuthorizationService auth,
	                   Translator translator)
	{
		this.auth = auth;
		this.translator = translator;
	}

	public AuthorizationService authorization()
	{
		return auth;
	}

	public Translator translator()
	{
		return translator;
	}
}
