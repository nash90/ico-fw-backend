package controllers;

import javax.inject.Inject;
import model.PasswordReset;
import common.filter.WithModel;
import common.filter.WithValidation;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import service.AuthenticationService;
import service.UserService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

@Transactional
public class AuthenticationController
    extends
        BaseController
{
	private static final String	SET_TRUE				= "true";
	private static final String	ACCESS_TOKEN			= "token";
	private static final String	PASSWORD_RESET_TOKEN	= "passreset_token";
	private static final String	PASSWORD_RESET_EMAIL	= "passreset_email";
	private static final String	QUERY_EMAIL				= "email";
	private static final String	QUERY_CODE				= "code";

	AuthenticationService						authService;
	UserService									userService;
	serviceImpl.ApplicationConfigurationService	config;

	@Inject
	public AuthenticationController(
	                                AuthenticationService authService,
	                                UserService userService,
	                                serviceImpl.ApplicationConfigurationService config)
	{
		this.authService = authService;
		this.userService = userService;
		this.config = config;
	}

	// --- METHODS --- //
	@WithModel(model.Login.class)
	@WithValidation(selective = false)
	public Result login()
	{

		session().clear();
		String token = authService.login(entity());
		response().setCookie(buildTokenCookie(token));
		session(ACCESS_TOKEN, token);
		return ok(Json.toJson(token));
	}

	public Result logout()
	{
		session().clear();
		response().discardCookie(ACCESS_TOKEN);
		return ok();
	}

	public Result getSession()
	{
		String session = session(ACCESS_TOKEN);
		if (session != null)
		{
			String token = getToken();
			if (token == null || token != session)
			{
				token = session;
				response().setCookie(buildTokenCookie(token));
			}
			return ok(token);
		} else
		{
			return badRequest();
		}
	}

	public Result activateUser()
	{
		String activateToken = request().getQueryString(QUERY_CODE);
		authService.activateUser(activateToken);
		return redirect("/auth/confirmation.html");
	}

	public Result resetPasswordRequest()
	{
		session().clear();
		String email = getFormBody().get(QUERY_EMAIL)[0];
		String resetToken = authService.resetPasswordRequest(email, getTranslator());
		session(PASSWORD_RESET_TOKEN, resetToken);
		session(PASSWORD_RESET_EMAIL, email);

		return ok();
	}

	@WithModel(PasswordReset.class)
	@WithValidation(selective = false)
	public Result resetPassword()
	{
		PasswordReset resetPass = entity();
		// TODO: PasswordReset object does not appear to have a getPassword
		// method call.
		userService.validatePassword(resetPass.getPassword(), getTranslator());

		String passresetToken = session(PASSWORD_RESET_TOKEN);
		String passresetEmail = session(PASSWORD_RESET_EMAIL);
		authService.resetPassword(passresetToken, passresetEmail, entity());
		return ok();
	}

	private Cookie buildTokenCookie(
	    String token)
	{
		String secureCookie = CONFIGURATION.SECURE_COOKIE.getValue();
		boolean secure = false;
		if (secureCookie.equals(SET_TRUE))
		{
			secure = true;
		}
		return Cookie.builder(ACCESS_TOKEN, token)
		             .withHttpOnly(true)
		             .withSecure(secure)
		             .build();
	}
}
