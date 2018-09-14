package common;

import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ErrorMessage
{

	// --- NESTED TYPES --- //

	public static enum Code
	{
			INTERNAL (
			    "Internall error"),
			INVALID_ID (
			    "Provided ID is not valid"),
			INVALID_INPUT (
			    "Input failed validation"),
			BAD_AUTHORIZATION (
			    "Unauthoirzed access"),
			BAD_UPDATE (
			    "Illegal transition of entity state"),
			CLIENT_ERROR (
			    "Client error");

		private final String defaultMessage;

		Code(
		     String defaultMessage)
		{
			this.defaultMessage = defaultMessage;
		}

		public String getDefaultMessage()
		{
			return defaultMessage;
		}
	}

	// --- STATIC FIELDS --- //

	private static final ObjectMapper MAPPER = new ObjectMapper();

	// --- STATIC METHODS --- //

	public static <T> ErrorMessage invalidId(
	    Class<T> entityType,
	    long id)
	{
		return invalidId(entityType, Long.toString(id));
	}

	public static <T> ErrorMessage invalidId(
	    Class<T> entityType,
	    Object id)
	{
		return new ErrorMessage(Code.INVALID_ID, "There is no " + entityType.getSimpleName() + " identified by \"" + id.toString() + "\"");
	}

	public static <T> ErrorMessage badAuthoirzation(
	    Class<T> entityType,
	    CRUD access)
	{
		return new ErrorMessage(Code.BAD_AUTHORIZATION, "Not enough credentials to perform " + access + " operation on " + entityType.getSimpleName() + " resource");
	}

	public static ErrorMessage badAuthoirzation(
	    String message)
	{
		return new ErrorMessage(Code.BAD_AUTHORIZATION, message);
	}

	public static ErrorMessage badUpdate(
	    String message)
	{
		return new ErrorMessage(Code.BAD_UPDATE, message);
	}

	public static ErrorMessage internal(
	    Throwable ex)
	{
		return new ErrorMessage(Code.INTERNAL, ex);
	}

	public static ErrorMessage internal(
	    String message)
	{
		return new ErrorMessage(Code.INTERNAL, message);
	}

	public static ErrorMessage invalidInput(
	    String what,
	    String why,
	    Object badValue)
	{
		if (badValue == null)
		{
			badValue = "null";
		}

		return new ErrorMessage(Code.INVALID_INPUT, what + " has invalid value \"" + badValue.toString() + "\": " + why);
	}

	public static ErrorMessage invalidInput(
	    String msg)
	{
		return new ErrorMessage(Code.INVALID_INPUT, msg);
	}

	public static ErrorMessage merge(
	    ErrorMessage first,
	    ErrorMessage second)
	{
		return merge(first, second, null);
	}

	public static ErrorMessage merge(
	    ErrorMessage first,
	    ErrorMessage second,
	    Code defaultCode)
	{
		if (first == null)
		{
			return second;
		}

		if (second == null)
		{
			return first;
		}

		ErrorMessage parent = first;

		if (first.details == null)
		{
			parent = new ErrorMessage((defaultCode == null) ? first.code : defaultCode);
			parent.addDetail(first);
		}

		if (second.details == null)
		{
			parent.addDetail(second);
		} else
		{
			for (ErrorMessage msg : second.details)
			{
				parent.addDetail(msg);
			}
		}

		return parent;
	}

	public static ErrorMessage merge(
	    ErrorMessage parent,
	    ValidationException exp)
	{
		return merge(parent, exp.getErrorMessage(), Code.INVALID_INPUT);
	}

	// --- FIELDS --- //

	private Code					code	= null;
	private String					message	= null;
	private String					target	= null;
	private ArrayList<ErrorMessage>	details	= null;

	// --- CONSTRUCTORS --- //

	public ErrorMessage()
	{
		code = Code.INTERNAL;
		message = "";
	}

	public ErrorMessage(
	                    String message)
	{
		code = Code.INTERNAL;
		this.message = message;
	}

	public ErrorMessage(
	                    Code code,
	                    String message)
	{
		this.code = code;
		this.message = message;
	}

	public ErrorMessage(
	                    Code code)
	{
		this.code = code;
		this.message = code.getDefaultMessage();
	}

	public ErrorMessage(
	                    Code code,
	                    String message,
	                    String target)
	{
		this.code = code;
		this.message = message;
		this.target = target;
	}

	public ErrorMessage(
	                    Throwable e)
	{
		this(Code.INTERNAL, e);
	}

	public ErrorMessage(
	                    Code code,
	                    Throwable e)
	{
		this.code = code;
		this.message = e.getClass()
		                .getCanonicalName() + ": " + e.getMessage();
		Throwable cause = e.getCause();
		if (cause != null)
		{
			this.addDetail(new ErrorMessage(code, cause));
		}
	}

	// --- METHODS --- //

	public Code getCode()
	{
		return code;
	}

	public void setCode(
	    Code code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(
	    String message)
	{
		this.message = message;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(
	    String target)
	{
		this.target = target;
	}

	public void addDetail(
	    ErrorMessage e)
	{
		if (details == null)
		{
			details = new ArrayList<ErrorMessage>();
		}
		details.add(e);
	}

	public Collection<ErrorMessage> getDetails()
	{
		return details;
	}

	public JsonNode toJson()
	{
		return MAPPER.valueToTree(this);
	}

	public ServerException toException()
	{
		return new ServerException(this);
	}

}
