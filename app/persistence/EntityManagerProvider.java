package persistence;

import javax.persistence.EntityManager;

public interface EntityManagerProvider
{
	/**
	 * Returns an EntityManager for the current Thread
	 */
	public EntityManager getEntityManager();
}
