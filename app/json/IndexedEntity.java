package json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Defines a Database Entity. Any DB entity must have an
 * ID field and a Version field.
 * 
 * The Version is used for Optimistic Locking, to ensure
 * data integrity in concurrent transactions.
 * 
 * Any Entity implementing this interface is serialized
 * automatically using the BaseEntitySerializer.
 */
@JsonSerialize(using = BaseEntitySerializer.class)
public interface IndexedEntity<T>
{
	T getId();

	void setId(
	    T id);

	Integer getVersion();

	void setVersion(
	    Integer date);
}
