package serviceImpl;

import javax.inject.Inject;
import model.User;
import common.ServerException;
import play.mvc.Http.Request;
import service.AuthorizationService;
import service.UserService;

public class AuthorizationServiceImpl
    implements
        AuthorizationService
{
	// --- FIELDS --- //

	private User loggedUser;

	private final String	ACCESS_TOKEN		= "token";
	private final String	INVALID_ASSET_OWNER	= "Authorization Failed: Not a valid owner of requested resource";
	private final String	NULL_ASSET			= "Authorization Failed: Invalid resource";
	private final String	INVALID_USER		= "Authorization Failed: Not logged in or invalid login token";

	UserService userService;

	// --- CONSTRUCTORS --- //

	/**
	 * Default constructor.
	 */
	@Inject
	public AuthorizationServiceImpl(
	                                UserService userService)
	{
		this.userService = userService;
	}

	public void initAuthorization(
	    Request request)
	{
		this.loggedUser = null;

		play.mvc.Http.Cookie tokenCookie = request.cookies()
		                                          .get(ACCESS_TOKEN);
		if (tokenCookie != null)
		{
			this.loggedUser = userService.getUserByToken(tokenCookie.value());
		}
	}

	@Override
	public boolean isValidOwner(
	    User assetOwner)
	{
		isLoggedIn();

		if (assetOwner != null)
		{
			if (assetOwner.equals(this.loggedUser))
			{
				return true;
			} else
			{
				throw new ServerException(INVALID_ASSET_OWNER);
			}
		} else
		{
			throw new ServerException(NULL_ASSET);
		}
	}

	@Override
	public long getValidOwnerId(
	    long id)
	{
		isLoggedIn();

		if (id == 0)
		{
			return this.loggedUser.getId();
		} else if (id == this.loggedUser.getId())
		{
			return id;
		} else
		{
			throw new ServerException(INVALID_ASSET_OWNER);
		}
	}

	@Override
	public User getLoggedUser()
	{
		return this.loggedUser;
	}

	@Override
	public boolean isLoggedIn()
	{
		if (this.loggedUser != null)
		{
			return true;
		} else
		{
			throw new ServerException(INVALID_USER);
		}
	}

}
