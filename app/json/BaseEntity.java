package json;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

@MappedSuperclass
@ToString
// @EqualsAndHashCode
public class BaseEntity
    implements
        IndexedEntity<Long>
{

	// --- FIELDS --- //

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@Column(name = "version")
	@Version
	@Hidden
	@JsonIgnore
	private Integer version = null;

	// --- METHODS --- //

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public void setId(
	    Long id)
	{
		this.id = id;
	}

	@Override
	public boolean equals(
	    Object other)
	{
		if (other == null)
		{
			return false;
		}

		if (other instanceof BaseEntity)
		{
			BaseEntity otherb = (BaseEntity) other;

			if (id == null && otherb.id == null)
			{
				return true;
			}

			if (id == null || otherb.id == null)
			{
				return false;
			}

			return otherb.getId()
			             .equals(id);
		}

		return false;
	}

	@Override
	public Integer getVersion()
	{
		return version;
	}

	@Override
	public void setVersion(
	    Integer version)
	{
		this.version = version;
	}

}
