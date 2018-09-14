package json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wraps a List from a given offset to a given length for te given List.
 * Designed for the purpose of database pagination.
 * 
 * @param <T>
 *            The Type parameter of the Partial List.
 */
public class PartialList<T>
{

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private long	offset	= 0;
	private long	length	= 0;
	private long	total	= 0;

	private List<T> items = Collections.emptyList();

	/**
	 * No Args constructor for Deserialization using jackson.
	 */
	public PartialList()
	{
		// TODO Auto-generated constructor stub
	}

	public PartialList(
	                   T[] items)
	{
		this.items = new ArrayList<>(Arrays.asList(items));
		offset = 0;
		total = items.length;
		length = items.length;
	}

	/**
	 * Creates a Partial that encapsulates a list with a given offset and total.
	 * 
	 * @param items
	 *            A list containing items.
	 * @param offset
	 *            Set the offset here.
	 * @param total
	 *            Set the total here.
	 */
	@SuppressWarnings("unchecked")
	public PartialList(
	                   List<?> items,
	                   long offset,
	                   long total)
	{
		this.items = new ArrayList<T>((List<T>) items);
		this.offset = offset;
		this.total = total;
		length = items.size();
	}

	/**
	 * Single args constructor, Initializes the Partial list from offset 0 to
	 * length equal to the size of the list.
	 * 
	 * @param items
	 *            A reference to a Class implementing List.
	 */
	@SuppressWarnings("unchecked")
	public PartialList(
	                   Collection<?> items)
	{
		this.items = new ArrayList<T>((List<T>) items);
		this.offset = 0;
		this.total = items.size();
		this.length = items.size();
	}

	/**
	 * Returns the offset with which the List was initialized.
	 * 
	 * @return The offset with which the List was initialized.
	 */
	public long getOffset()
	{
		return offset;
	}

	/**
	 * Return the length of the Partial List.
	 * 
	 * @return The size of the containing list.
	 */
	public long getLength()
	{
		return length;
	}

	/**
	 * Returns the total number of elements if the list was initialized, else
	 * returns
	 * the size of the list.
	 * 
	 * @return The total the list was initialized with or the size of the list.
	 */
	public long getTotal()
	{
		return total;
	}

	/**
	 * Returns the list wrapped by the PartialList.
	 * 
	 * @return The list wrapped by the PartialList.
	 */
	public List<T> getItems()
	{
		return items;
	}

	/**
	 * Returns a JsonNode representation of the current class.
	 * 
	 * @return JsonNode The JsonNode representation of the list.
	 */
	public JsonNode toJson()
	{
		return MAPPER.valueToTree(this);
	}

}
