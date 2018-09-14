package persistenceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import persistence.EntityManagerProvider;
import play.db.jpa.JPAApi;

/**
 * Uses Play's JPA integration to provide instances of EntityManager on demand.
 */
@Singleton
public class PlayEntityManagerProvider
    implements
        EntityManagerProvider
{

	private JPAApi jpaApi;

	// --- CONSTRUCTORS --- //

	@Inject
	public PlayEntityManagerProvider(
	                                 JPAApi jpaApi)
	{
		this.jpaApi = jpaApi;

	}

	// --- METHODS --- //

	/**
	 * Returns an instance of EntityManager linked to the current HTTP context.
	 * 
	 * This method must be invoked after a Controller that has been annotated
	 * with @Transactional or from within a JPAApi.withTransaction() method,
	 * otherwise will throw an exception.
	 */
	@Override
	public EntityManager getEntityManager()
	{
		return jpaApi.em();
	}
}
