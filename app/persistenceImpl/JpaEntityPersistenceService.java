package persistenceImpl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import common.DatabaseException;
import json.IndexedEntity;
import json.PartialList;
import persistence.EntityManagerProvider;
import persistence.EntityPersistenceService;

/**
 * Generic JPA Persistence Service. Inherit from this class to declare a
 * Persistence Service specific to
 * a particular Entity type.
 * 
 * It provides generic methods for common use cases (add, delete,...), and
 * children classes provide
 * additional methods for specific Entity types.
 */
public class JpaEntityPersistenceService<T extends IndexedEntity<?>>
    implements
        EntityPersistenceService<T>
{

	// --- NESTED TYPES --- //

	protected static interface QueryHelper<T>
	{
		void run(
		    CriteriaQuery<?> cq,
		    CriteriaBuilder cb,
		    Root<T> root);
	}

	// --- FIELDS --- //

	private EntityManagerProvider p;

	private final Class<T> entityClass;

	// --- CONSTRUCTORS --- //

	public JpaEntityPersistenceService(
	                                   EntityManagerProvider p,
	                                   Class<T> entityClass)
	{
		this.p = p;
		this.entityClass = entityClass;
	}

	// --- METHODS --- //

	/**
	 * Returns All entries in a table, restricted by offset and size
	 */
	@Override
	public PartialList<T> getAll(
	    long offset,
	    long size)
	{

		final CriteriaQuery<T> query = getManager().getCriteriaBuilder()
		                                           .createQuery(this.entityClass);
		query.from(this.entityClass);

		TypedQuery<T> q = getManager().createQuery(query)
		                              .setFirstResult((int) offset);

		if (size > 0)
		{
			q.setMaxResults((int) size);
		}

		List<T> result = q.getResultList();
		return new PartialList<>(result, offset, count());
	}

	/**
	 * Retrieves an entity by ID.
	 * 
	 * @throws NoResultException
	 *             if the Entity is not found
	 */
	@Override
	public T getById(
	    Object id)
	{
		return getManager().find(this.entityClass, id);
	}

	/**
	 * Saves a new entity to the Database, generating its ID.
	 * 
	 * The given obj remains attached to the Persistence context, so
	 * further changes to the entity will also be persisted when the
	 * transaction is commited.
	 * 
	 * @param obj
	 *            The entity to persist. Its ID must be Null.
	 */
	@Override
	public void add(
	    T obj)
	    throws DatabaseException
	{
		try
		{
			getManager().persist(obj);
			// Generate IDs
			getManager().flush();
		} catch (PersistenceException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * Updates the given Entity in the database. The ID of the passed entity
	 * is used to locate the current row in the database, and all its fields
	 * are over-written by the fields in obj.
	 * 
	 * @param obj
	 *            The entity to update. ID must exist in the Database.
	 * @throws DatabaseException
	 *             if the Entity is not found in the Database
	 *             or an error occurs during update.
	 */
	@Override
	public T update(
	    T obj)
	    throws DatabaseException
	{
		try
		{
			T previousRecord = getManager().find(entityClass, obj.getId());
			if (previousRecord == null)
			{
				throw new DatabaseException("Entity " + obj + " not found");
			}

			obj.setVersion(previousRecord.getVersion());
			return getManager().merge(obj);
		} catch (PersistenceException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * Removes an entity from the Database.
	 * 
	 * @throws ServerException
	 */
	@Override
	public void delete(
	    T obj)
	    throws DatabaseException
	{
		try
		{
			getManager().remove(obj);
		} catch (PersistenceException e)
		{
			throw new DatabaseException(e);
		}
	}

	protected EntityManager getManager()
	{
		return p.getEntityManager();
	}

	protected long count()
	{
		final CriteriaQuery<Long> query = getManager().getCriteriaBuilder()
		                                              .createQuery(Long.class);
		final Root<T> root = query.from(this.entityClass);
		query.select(getManager().getCriteriaBuilder()
		                         .count(root));
		return (Long) getManager().createQuery(query)
		                          .getSingleResult();
	}

	/**
	 * Removes all Entities, USE WITH CAUTION
	 * 
	 * @return the number of rows deleted
	 */
	@Override
	public long deleteAll()
	    throws DatabaseException
	{
		PartialList<T> list = getAll(0, -1);
		for (T entity : list.getItems())
		{
			delete(entity);
		}
		return list.getTotal();
	}

	protected PartialList<T> performQuery(
	    QueryHelper<T> h,
	    int offset,
	    int size)
	{
		EntityManager em = getManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> root = cq.from(entityClass);

		long count = 0;
		List<?> list = null;

		root.alias("query");
		h.run(cq, cb, root);

		if (size > 0)
		{

			TypedQuery<T> q = em.createQuery(cq);
			q.setFirstResult(offset);
			q.setMaxResults(size);

			// Logger.debug("QUERY : " +
			// q.unwrap(QueryImpl.class).getHibernateQuery().getQueryString());

			list = q.getResultList();

			count = countWithCondition(h);

		} else
		{
			TypedQuery<T> q = em.createQuery(cq);
			q.setFirstResult(offset);
			list = q.getResultList();
			count = (long) list.size();
		}

		return new PartialList<>(list, offset, count);
	}

	protected long countWithCondition(
	    QueryHelper<T> h)
	{
		EntityManager em = getManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> root = cq.from(entityClass);

		h.run(cq, cb, root);
		cq.select(getManager().getCriteriaBuilder()
		                      .count(root));

		return (Long) getManager().createQuery(cq)
		                          .getSingleResult();
	}

	protected T performSingleResultQuery(
	    QueryHelper<T> h)
	{
		EntityManager em = getManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> root = cq.from(entityClass);

		h.run(cq, cb, root);

		List<T> result = em.createQuery(cq)
		                   .setMaxResults(1)
		                   .getResultList();
		if (result.isEmpty())
		{
			return null;
		}
		return result.get(0);
	}

	/**
	 * Gets a reference to any Entity by Primary Key.
	 * Useful for resolving Foreign Key relationships.
	 * 
	 * Returns null if the given ID is null
	 * 
	 * @param type
	 *            Class of the Entity
	 * @param id
	 *            Primary Key
	 * @return Entity
	 */
	protected <E> E getReference(
	    Class<E> type,
	    Object id)
	{
		return (id == null) ? null : getManager().getReference(type, id);
	}

	/**
	 * Saves the given Entity to Database. If previous is null, then it will add
	 * it as
	 * new entry. Otherwise, it will merge() the given entity into the previous.
	 * 
	 * @param previous
	 *            Previous value of the field (attached to persistence context).
	 * @param save
	 *            New entity (detached).
	 * @return The saved entity. If a new entry to the table, the ID is
	 *         generated.
	 */
	protected <E extends IndexedEntity<ID>, ID> E saveChildEntity(
	    E previous,
	    E save)
	{
		if (previous == null)
		{
			// New Review
			save.setId(null);
			getManager().persist(save);
			getManager().flush();
			return save;

		} else
		{
			// Update existing
			save.setId(previous.getId());
			save.setVersion(previous.getVersion());
			E merged = getManager().merge(save);
			return merged;
		}
	}
}
