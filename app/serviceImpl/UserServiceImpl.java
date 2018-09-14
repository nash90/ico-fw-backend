package serviceImpl;

import java.util.Base64;
import java.util.Calendar;
import java.util.Random;
import javax.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import model.PasswordUpdate;
import model.User;
import model.User.Status;
import model.UserApiModel;
import common.ErrorMessage;
import common.ServerException;
import common.Translator;
import persistence.UserPersistenceService;
import service.UserService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class UserServiceImpl
    implements
        UserService
{

	// --- FIELDS --- //
	public final static int	DAY										= 60 * 60 * 24;
	private final String	EMAIL_ALREADY_EXIST						= "Email Already Exists";
	private final String	USER_ACTIVATE_URI						= "/api/activate?code=";
	private final String	ACCOUNT_CREATED							= "Account Created: Please Activate your account";
	private final String	INCORRECT_PASSWORD						= "Wrong Password";
	private final String	PASSWORD_IS_REQUIRED					= "PASSWORD_IS_REQUIRED";
	private final String	VALIDATION_PASSWORD_TOO_SHORT			= "VALIDATION_PASSWORD_TOO_SHORT(n)";
	private final String	VALIDATION_PASSWORD_WITHOUT_NUMERIC		= "VALIDATION_PASSWORD_WITHOUT_NUMERIC";
	private final String	VALIDATION_PASSWORD_WITHOUT_ALPHABETIC	= "VALIDATION_PASSWORD_WITHOUT_ALPHABETIC";
	private final String	RESTIRCTED_FEILD						= "Restricted Field update attempted!!";
	private final String	EMAIL_VERIFICATION						= "EMAIL_VERIFICATION(name, url)";

	// --- FIELDS --- //

	private final UserPersistenceService						userPs;
	private final EmailService									emailService;
	private final serviceImpl.ApplicationConfigurationService	config;

	// --- CONSTRUCTORS --- //

	@Inject
	public UserServiceImpl(
	                       UserPersistenceService userPersistenceService,
	                       EmailService emailService,
	                       serviceImpl.ApplicationConfigurationService config)
	{
		this.userPs = userPersistenceService;
		this.emailService = emailService;
		this.config = config;
	}

	@Override
	public User create(
	    UserApiModel userApiModel)
	{
		User createdUser = new User();
		if (userPs.getUserByEmail(userApiModel.getEmail()) == null)
		{
			userApiModel.setPassword(getPasswordHash(userApiModel.getPassword()));
			createdUser = getUserFromUserApiModel(userApiModel);
			createdUser.setStatus(Status.PENDING_VERIFICATION);
			createdUser.setActivateToken(createActivationToken(DAY));
			userPs.add(createdUser);
		} else
		{
			throw new ServerException(new ErrorMessage(ErrorMessage.Code.CLIENT_ERROR, EMAIL_ALREADY_EXIST));
		}
		return createdUser;
	}

	@Override
	public User getById(
	    long id)
	{
		User user = userPs.getById(id);
		return user;
	}

	@Override
	public User getUserByEmail(
	    String email)
	{
		User user = userPs.getUserByEmail(email);
		return user;
	}

	@Override
	public User getUserByToken(
	    String loginToken)
	{
		User user = userPs.getUserByToken(loginToken);
		return user;
	}

	@Override
	public User getUserByActivateToken(
	    String loginToken)
	{
		User user = userPs.getUserByActivateToken(loginToken);
		return user;
	}

	@Override
	public User update(
	    long id,
	    UserApiModel userApiModel)
	{
		User savedUser = userPs.getById(id);

		User user = userPs.update(updateUserFromUserApiModel(savedUser, userApiModel));
		return user;
	}

	@Override
	public void updatePassword(
	    long id,
	    PasswordUpdate pass)
	{
		User savedUser = userPs.getById(id);
		if (!checkPassword(pass.getOldPassword(), savedUser.getPassword()))
		{
			throw new ServerException(INCORRECT_PASSWORD);
		}
		savedUser.setPassword(getPasswordHash(pass.getNewPassword()));
		userPs.update(savedUser);
	}

	@Override
	public String generateRandomString()
	{
		Random rnd = new Random();
		char[] chars = new char[128];
		for (int i = chars.length - 1; i >= 0; i--)
		{
			chars[i] = (char) ((int) '!' + rnd.nextInt(90));
		}

		String tokenString = new String(chars);
		tokenString = Base64.getUrlEncoder()
		                    .encodeToString(tokenString.getBytes());
		return tokenString;
	}

	@Override
	public void sendUserCreateEmail(
	    User user,
	    Translator tran)
	{
		String url = CONFIGURATION.SITE_URL.getValue() + USER_ACTIVATE_URI + user.getActivateToken();
		String from = CONFIGURATION.CONTACT_EMAIL.getValue();
		String to = user.getEmail();
		String body = tran.get(EMAIL_VERIFICATION, user.getName(), url);
		emailService.sendGeneralEmail(from, to, ACCOUNT_CREATED, body);
	}

	@Override
	public String createActivationToken(
	    int timeInMicro)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, timeInMicro);
		String expiry = String.valueOf(calendar.getTimeInMillis());
		String token = generateRandomString() + "." + expiry;
		return token;
	}

	@Override
	public void validatePassword(
	    String password,
	    Translator t)
	{
		ErrorMessage passwordError = getPasswordError(password, t);
		if (passwordError != null)
		{
			throw new ServerException(passwordError);
		}
	}

	public ErrorMessage getPasswordError(
	    String password,
	    Translator t)
	{
		ErrorMessage result = null;
		if (password == null)
		{
			return ErrorMessage.invalidInput(t.get(PASSWORD_IS_REQUIRED));
		}
		if (password.length() == 0)
		{
			return ErrorMessage.invalidInput(t.get(PASSWORD_IS_REQUIRED));
		}
		if (password.length() < 6)
		{
			result = ErrorMessage.merge(result, ErrorMessage.invalidInput(t.get(VALIDATION_PASSWORD_TOO_SHORT, 6)));
		}
		if (!password.matches("^(?=.*\\d).+$"))
		{
			result = ErrorMessage.merge(result, ErrorMessage.invalidInput(t.get(VALIDATION_PASSWORD_WITHOUT_NUMERIC)));
		}
		if (!password.matches("^(?=.*[a-zA-Z]).+$"))
		{
			result = ErrorMessage.merge(result, ErrorMessage.invalidInput(t.get(VALIDATION_PASSWORD_WITHOUT_ALPHABETIC)));
		}
		return result;
	}

	private User getUserFromUserApiModel(
	    UserApiModel userApiModel)
	{
		User user = new User();
		user.setEmail(userApiModel.getEmail());
		user.setName(userApiModel.getName());
		user.setPassword(userApiModel.getPassword());
		user.setWalletAddress(userApiModel.getWalletAddress());
		return user;
	}

	private User updateUserFromUserApiModel(
	    User user,
	    UserApiModel userApiModel)
	{
		if (userApiModel.getPassword() != null)
		{
			throw new ServerException(RESTIRCTED_FEILD);
		}

		if (userApiModel.getName() != null)
		{
			user.setName(userApiModel.getName());
		}

		if (userApiModel.getEmail() != null)
		{
			// TODO: check requirement if update to email by user is allowed
			// user.setEmail(userApiModel.getEmail());
		}

		if (userApiModel.getWalletAddress() != null)
		{
			user.setWalletAddress(userApiModel.getWalletAddress());
		}
		return user;
	}

	private String getPasswordHash(
	    String rawPassword)
	{
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
	}

	private boolean checkPassword(
	    String plainPassword,
	    String hashPassword)
	{
		return BCrypt.checkpw(plainPassword, hashPassword);
	}

}
