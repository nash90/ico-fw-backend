package persistenceImpl;

import javax.inject.Inject;
import model.User;
import persistence.EntityManagerProvider;
import persistence.UserPersistenceService;

public class UserPersistenceServiceImpl
    extends
        JpaEntityPersistenceService<User>
    implements
        UserPersistenceService
{
	@Inject
	public UserPersistenceServiceImpl(
	                                  EntityManagerProvider p)
	{
		super(p, User.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public User getUserByEmail(
	    String email)
	{

		if (email == null)
		{
			return null;
		}
		return performSingleResultQuery((
		    cq,
		    cb,
		    root) -> {
			cq.where(cb.equal(cb.lower(root.get("email")), email.toLowerCase()));
		});
	}

	@Override
	public User getUserByToken(
	    String token)
	{
		if (token == null)
		{
			return null;
		}
		return performSingleResultQuery((
		    cq,
		    cb,
		    root) -> {
			cq.where(cb.equal(root.get("loginToken"), token));
		});
	}

	@Override
	public User getUserByActivateToken(
	    String activateToken)
	{
		if (activateToken == null)
		{
			return null;
		}
		return performSingleResultQuery((
		    cq,
		    cb,
		    root) -> {
			cq.where(cb.equal(root.get("activateToken"), activateToken));
		});
	}

}
