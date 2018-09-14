/*--------------------------------------------------------------*\.
 |
 | Project:   m-vacation
 | Declares:  BaseEntitySerializer
 | Author(s): rafael (rafael@bluewall.jpn.com)
 |
 | Copyright (C) 2017 BlueWall Japan Inc. All rights reserved
 |
 *///////////////////////////////////////////////////////////////
package json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.proxy.HibernateProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javassist.Modifier;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;

/**
 * Converts a BaseEntity to Json. It serializes all fields declared in an
 * Entity, except static fields, which are ignored.
 * 
 * The following annotations can be used to configure the behavior of the
 * serializer:
 * 
 * \@Hidden Doesn't include a field in the Json
 * \@IdOnly When used in entity fields, it will only include "field_id" : "1"
 * When user in Collections, it will only include a list of ids of the
 * listed entities, eg: "field":[1,2,3]
 */
public class BaseEntitySerializer<T>
    extends
        StdSerializer<T>
{

	// --- STATIC FIELDS --- //

	private static final long				serialVersionUID	= -8366827514503524577L;
	private static final SimpleDateFormat	dateFormatter		= new SimpleDateFormat("yyyy-MM-dd");

	// --- FIELDS --- //

	private boolean publicOnly = false;

	// --- CONSTRUCTORS --- //

	protected BaseEntitySerializer(
	                               Class<T> t)
	{
		super(t, false);
	}

	protected BaseEntitySerializer()
	{
		this(null, false);
	}

	protected BaseEntitySerializer(
	                               boolean publicOnly)
	{
		this(null, publicOnly);
	}

	protected BaseEntitySerializer(
	                               Class<T> t,
	                               boolean publicOnly)
	{
		super(t);
		this.publicOnly = publicOnly;
	}

	// --- METHODS --- //

	@Override
	public void serialize(
	    T value,
	    JsonGenerator gen,
	    SerializerProvider provider)
	    throws IOException
	{
		serialize(gen, value, false);
	}

	private void serialize(
	    JsonGenerator gen,
	    Object value,
	    boolean idOnly)
	    throws IOException
	{
		if (value == null)
		{
			gen.writeNull();
			return;
		}

		value = beforeParse(value);

		gen.writeStartObject();
		try
		{
			// ID
			if (IndexedEntity.class.isAssignableFrom(value.getClass()))
			{
				writeField(gen, "id", ((IndexedEntity<?>) value).getId());
			}

			// Fields
			if (!idOnly)
			{

				for (Field f : value.getClass()
				                    .getDeclaredFields())
				{
					if (f.isAnnotationPresent(Hidden.class) || f.isSynthetic() || Modifier.isStatic(f.getModifiers()))
					{
						continue;
					}

					if (publicOnly && f.isAnnotationPresent(PrivateToOwner.class))
					{
						continue;
					}

					Object fieldValue = FieldUtils.readField(value, f.getName(), true);
					if (Collection.class.isAssignableFrom(f.getType()))
					{

						serializeCollectionField(gen, f, fieldValue);

					} else if (IndexedEntity.class.isAssignableFrom(f.getType()))
					{

						gen.writeFieldName(f.getName());
						serialize(gen, fieldValue, f.isAnnotationPresent(IdOnly.class));

					} else if (f.isAnnotationPresent(DateFormat.class))
					{

						if (fieldValue != null)
						{
							gen.writeStringField(f.getName(), dateFormatter.format((Date) fieldValue));
						} else
						{
							gen.writeNullField(f.getName());
						}

					} else if (f.isAnnotationPresent(JsonFromGetter.class))
					{

						writeField(gen, f.getName(), valueFromGetter(f.getName(), value));

					} else
					{

						writeField(gen, f.getName(), fieldValue);

					}
				}
			}
		} catch (Exception e)
		{
			String msg = "Error parsing to Json entity:" + value + "\n";
			Logger.error(msg);
			throw new IOException(msg + e.getMessage());
		}
		gen.writeEndObject();
	}

	/**
	 * Makes any necessary checks and data handling of the object before the
	 * actual Json generation.
	 * 
	 * @param value
	 */
	private Object beforeParse(
	    Object value)
	{
		Object ret = value;

		// If this field is a LAZY loaded relationship, load the actual entity
		if (value instanceof HibernateProxy)
		{
			ret = ((HibernateProxy) value).getHibernateLazyInitializer()
			                              .getImplementation();
		}

		// Translations
		if (TranslatedEntity.class.isAssignableFrom(value.getClass()))
		{
			((TranslatedEntity) ret).useLocale(locale());
		}

		return ret;
	}

	private void writeField(
	    JsonGenerator gen,
	    String name,
	    Object value)
	    throws IOException
	{
		if (value == null)
		{
			gen.writeNullField(name);
		} else
		{
			gen.writeObjectField(name, Json.toJson(value));
		}
	}

	private void serializeCollectionField(
	    JsonGenerator gen,
	    Field f,
	    Object fieldValue)
	    throws IOException
	{
		if (fieldValue == null)
		{
			gen.writeNullField(f.getName());
		} else
		{
			gen.writeArrayFieldStart(f.getName());
			for (Object listEntry : ((Collection<?>) fieldValue))
			{
				serialize(gen, listEntry, f.isAnnotationPresent(IdOnly.class));
			}
			gen.writeEndArray();
		}
	}

	private Object valueFromGetter(
	    String name,
	    Object parentInstance)
	    throws Exception
	{
		Method getter = parentInstance.getClass()
		                              .getMethod("get" + StringUtils.capitalize(name));
		return getter.invoke(parentInstance);
	}

	private String locale()
	{
		String locale = null;
		Context ctx = Http.Context.current();
		if (ctx != null)
		{
			locale = ctx.request()
			            .getQueryString("lang");
			if (locale == null)
			{
				// Attempt form cookie
				Cookie langCookie = ctx.request()
				                       .cookies()
				                       .get("lang_code");
				if (langCookie != null)
				{
					locale = langCookie.value();
				}
			}
		}
		return locale;
	}

}
