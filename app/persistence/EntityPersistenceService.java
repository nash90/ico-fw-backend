package persistence;

import common.DatabaseException;
import json.PartialList;

public interface EntityPersistenceService<T>
{
	public void add(
	    T obj)
	    throws DatabaseException;

	public T update(
	    T obj)
	    throws DatabaseException;

	public void delete(
	    T obj)
	    throws DatabaseException;

	public long deleteAll()
	    throws DatabaseException;

	public PartialList<T> getAll(
	    long offset,
	    long size);

	public T getById(
	    Object id);
}
