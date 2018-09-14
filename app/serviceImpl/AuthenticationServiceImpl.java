package serviceImpl;

import javax.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import model.Login;
import model.PasswordReset;
import model.User;
import model.User.Status;
import common.ServerException;
import common.Translator;
import service.AuthenticationService;
import service.UserService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class AuthenticationServiceImpl
    implements
        AuthenticationService
{
	private static final String	PASSWORD_RESET_URI				= "/auth/passreset.html?code=";
	private static final String	INVALID_ACCOUNT					= "Not a valid user account";
	private static final String	INVALID_EMAIL					= "Not a valid email";
	private static final String	INVALID_ACCOUNT_VARIFICATION	= "Account has not been varified";
	private static final String	UNAUTHORIZED_ACCOUNT_STAUTS		= "Account Status is not authorized to access resource";
	private static final String	WRONG_PASSWORD					= "Wrong Account information";
	private static final String	INVALID_ACTIVATION				= "Activation Code is invalid";
	private static final String	PASSWORD_RESET					= "Reset your password";
	private static final String	WRONG_EMAIL_RESET_TOKEN			= "Email or reset token is invalid";
	private static final String	PASSWORD_RESET_TOKEN_EXPIRED	= "Password reset token expired, please request for reset again";
	public final static int		DAY								= 60 * 60 * 24;

	final UserService									userService;
	final serviceImpl.ApplicationConfigurationService	config;
	final EmailService									emailService;

	@Inject
	public AuthenticationServiceImpl(
	                                 UserService userService,
	                                 serviceImpl.ApplicationConfigurationService config,
	                                 EmailService emailService)
	{
		super();
		this.userService = userService;
		this.config = config;
		this.emailService = emailService;
	}

	@Override
	public String login(
	    Login login)
	{
		User userAsset = userService.getUserByEmail(login.getEmail());

		if (userAsset != null)
		{
			Status userStatus = userAsset.getStatus();
			if (!userStatus.equals(Status.ACTIVE))
			{
				if (userStatus.equals(Status.PENDING_VERIFICATION))
				{
					throw new ServerException(INVALID_ACCOUNT_VARIFICATION);
				} else
				{
					throw new ServerException(UNAUTHORIZED_ACCOUNT_STAUTS);
				}
			}

			String hashPassword = userAsset.getPassword();
			if (validatePassword(login.getPassword(), hashPassword))
			{
				String loginToken = createLoginToken();
				userAsset.setLoginToken(loginToken);
				return loginToken;
			} else
			{
				throw new ServerException(WRONG_PASSWORD);
			}

		} else
		{
			throw new ServerException(INVALID_ACCOUNT);
		}
	}

	private boolean validatePassword(
	    String plainPassword,
	    String hashPassword)
	{
		return BCrypt.checkpw(plainPassword, hashPassword);

	}

	private String createLoginToken()
	{
		return userService.generateRandomString();
	}

	@Override
	public User activateUser(
	    String activateToken)
	{
		User userAsset = userService.getUserByActivateToken(activateToken);
		if (userAsset != null)
		{
			userAsset.setStatus(Status.ACTIVE);
		} else
		{
			throw new ServerException(INVALID_ACTIVATION);
		}
		return userAsset;
	}

	@Override
	public String resetPasswordRequest(
	    String email,
	    Translator tran)
	{
		String resetToken = null;
		if (email != null)
		{
			User userAsset = userService.getUserByEmail(email);
			if (userAsset != null)
			{
				resetToken = userService.createActivationToken(DAY);
				sendPasswordResetEmail(userAsset, tran, resetToken);
			} else
			{
				throw new ServerException(INVALID_ACCOUNT);
			}
		} else
		{
			throw new ServerException(INVALID_EMAIL);
		}
		return resetToken;
	}

	public void sendPasswordResetEmail(
	    User user,
	    Translator tran,
	    String token)
	{
		String url = CONFIGURATION.SITE_URL.getValue() + PASSWORD_RESET_URI + token;
		String from = CONFIGURATION.CONTACT_EMAIL.getValue();
		// TODO: Missing getEmail method in User Object
		String to = user.getEmail();
		// TODO: Missing getName method in User Object
		String body = tran.get("EMAIL_RECOVERY(name, url)", user.getName(), url);
		emailService.sendGeneralEmail(from, to, PASSWORD_RESET, body);
	}

	@Override
	public User resetPassword(
	    String token,
	    String emailInToken,
	    PasswordReset passwordreset)
	{
		User userAsset = null;
		if (token != null && emailInToken != null)
		{
			if (emailInToken.equals(passwordreset.getEmail()) && token.equals(passwordreset.getCode()))
			{
				userAsset = userService.getUserByEmail(emailInToken);
				if (userAsset != null)
				{
					userAsset.setPassword(getPasswordHash(passwordreset.getPassword()));
				} else
				{
					throw new ServerException(INVALID_ACCOUNT);
				}
			} else
			{
				throw new ServerException(WRONG_EMAIL_RESET_TOKEN);
			}

		} else
		{
			throw new ServerException(PASSWORD_RESET_TOKEN_EXPIRED);
		}
		return userAsset;
	}

	public String getPasswordHash(
	    String rawPassword)
	{
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
	}
}
