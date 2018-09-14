package service;

import com.google.inject.ImplementedBy;
import model.User;
import play.mvc.Http.Request;
import serviceImpl.AuthorizationServiceImpl;

/**
 * Encodes authorization of the current user for manipulating any given entity.
 */

@ImplementedBy(AuthorizationServiceImpl.class)
public interface AuthorizationService
{
	/**
	 * Returns the Node entity corresponding the current user. Returns null if
	 * the current user does not have
	 * a valid token.
	 * 
	 * @return
	 */
	public User getLoggedUser();

	public boolean isValidOwner(
	    User obj);

	public long getValidOwnerId(
	    long id);

	public boolean isLoggedIn();

	public void initAuthorization(
	    Request request);
}
