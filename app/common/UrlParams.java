package common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent the query parameters in the URL.
 * Also contains a lot of static utility functions to help Query Parameter
 * processing
 */
public class UrlParams
{

	/**
	 * Fetch value as string from params.
	 * 
	 * @param params
	 *            The query params from which String should be got
	 * @param key
	 *            The key for the param to get
	 * @param defaultValue
	 *            The default String value to return in case no value is
	 *            found corresponding to the key
	 * @return The String value of the value
	 */
	public static String getString(
	    Map<String, String[]> params,
	    String key,
	    String defaultValue)
	{
		String[] values = params.getOrDefault(key, null);
		if (values == null)
		{
			return defaultValue;
		}
		if (values.length == 0)
		{
			return defaultValue;
		}
		return values[0];
	}

	/**
	 * Fetch value as Integer from params.
	 * 
	 * @param params
	 *            The query params from which String should be got
	 * @param key
	 *            The key for the param to get
	 * @param defaultValue
	 *            The default Integer value to return in case no value is
	 *            found corresponding to the key
	 * @return The Integer value of the parameter or defaultValue if not found
	 * @throws ValidationException
	 *             If not a valid Integer
	 */
	public static Integer getInteger(
	    Map<String, String[]> params,
	    String key,
	    Integer defaultValue)
	    throws ValidationException
	{
		String str = getString(params, key, null);
		if (str == null)
		{
			return defaultValue;
		}

		try
		{
			return new Integer(str);
		} catch (NumberFormatException ex)
		{
			throw new ValidationException(key, "invalid number format", str);
		}
	}

	/**
	 * Fetch value as Long from params.
	 * 
	 * @param params
	 *            The query params from which String should be got
	 * @param key
	 *            The key for the param to get
	 * @param defaultValue
	 *            The default Long value to return in case no value is found
	 *            corresponding to the key
	 * @return The long value stored corresponding to key, In case of multiple
	 *         values
	 *         the first value is returned.
	 * @throws ValidationException
	 *             If not a valid Long
	 */
	public static Long getLong(
	    Map<String, String[]> params,
	    String key,
	    Long defaultValue)
	    throws ValidationException
	{
		String str = getString(params, key, null);
		if (str == null)
		{
			return defaultValue;
		}

		try
		{
			return new Long(str);
		} catch (NumberFormatException ex)
		{
			throw new ValidationException(key, "invalid number format", str);
		}
	}

	/**
	 * Add a param to the given map.
	 * The corresponding toString() value for object
	 * will be added for non-array objects.
	 * 
	 * @param params
	 *            The map to which param is to be added
	 * @param key
	 *            What key to use while adding value
	 * @param value
	 *            The corresponding value to add
	 */
	public static void put(
	    Map<String, String[]> params,
	    String key,
	    Object value)
	{
		if (value == null)
		{
			return;
		}
		String[] values = params.get(key);
		if (values == null)
		{
			params.put(key, new String[]
				{
				        value.toString()
				});
		} else
		{
			// Ref to values changes here
			values = Arrays.copyOf(values, values.length + 1);
			values[values.length - 1] = value.toString();
			params.put(key, values);
		}
	}

	/**
	 * Returns the URL query String from the given map.
	 * 
	 * @param map
	 *            The query parameters represented as a map.
	 * @return The query String representation of the map.
	 */
	public static String mapToQueryString(
	    Map<String, String[]> map)
	{
		StringBuilder builder = new StringBuilder();
		boolean notFirst = false;
		for (Map.Entry<String, String[]> pair : map.entrySet())
		{
			if (pair.getValue() == null)
			{
				continue;
			}
			for (String value : pair.getValue())
			{
				if (notFirst)
				{
					builder.append('&');
				}
				notFirst = true;

				try
				{
					builder.append(pair.getKey())
					       .append('=')
					       .append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException ex)
				{
					throw new Error("This is not supposed to happen", ex);
				}

			}
		}
		return builder.toString();
	}

	private final HashMap<String, String[]> params = new HashMap<>();

	/**
	 * No args constructor.
	 */
	public UrlParams()
	{
		// Nothing;
	}

	/**
	 * Set a value corresponding to key in current class.
	 * For multiple values under the same key call the function multiple times.
	 * 
	 * @param key
	 *            The key corresponding to which value is to be set.
	 * @param value
	 *            THe value to set/add corresponding to the key.
	 */
	public void set(
	    String key,
	    Object value)
	{
		put(params, key, value);
	}

	/**
	 * Returns Integer value from current class Map.
	 * 
	 * @param key
	 *            The key corresponding to which value is to be fetched.
	 * @return The integer value corresponding to the QueryParam key.
	 * @throws ValidationException
	 *             If not a valid Integer
	 */
	public Integer getInteger(
	    String key)
	    throws ValidationException
	{
		return getInteger(params, key, null);
	}

	/**
	 * Returns Long value from current Class Map.
	 * 
	 * @param key
	 *            The key corresponding to which value is to be fetched.
	 * @return The long value corresponding to the QueryParam key.
	 * @throws ValidationException
	 *             If not a valid Long
	 */
	public Long getLong(
	    String key)
	    throws ValidationException
	{
		return getLong(params, key, null);
	}

	/**
	 * Returns the value corresponding to the key in the present Class Map.
	 * 
	 * @param key
	 *            The key corresponding to which value is to be fetched.
	 * @return The String value corresponding to the QueryParam key.
	 */
	public String getString(
	    String key)
	{
		return getString(params, key, null);
	}

	/**
	 * Returns the url query string for current class param map.
	 * 
	 * @return Convert the QueryMap represented by class to Encoded url query
	 *         String.
	 */
	public String toEncodedString()
	{
		return mapToQueryString(params);
	}
}
