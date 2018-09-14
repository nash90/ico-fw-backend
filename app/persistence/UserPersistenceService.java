package persistence;

import com.google.inject.ImplementedBy;
import model.User;
import persistenceImpl.UserPersistenceServiceImpl;

@ImplementedBy(UserPersistenceServiceImpl.class)
public interface UserPersistenceService
    extends
        EntityPersistenceService<User>
{
	public User getUserByEmail(
	    String email);

	public User getUserByToken(
	    String token);

	public User getUserByActivateToken(
	    String activateToken);
}
