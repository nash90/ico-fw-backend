package common;

/**
 * This exception is thrown when input data is invalid.
 * The error code is INVALID_INPUT
 */
public class ValidationException
    extends
        Exception
{

	private static final long	serialVersionUID	= -9113240004096595911L;
	private ErrorMessage		msg;

	/**
	 * Default Constructor ErrorMessage.code will be INVALID_INPUT.
	 */
	public ValidationException()
	{
		msg = new ErrorMessage(ErrorMessage.Code.INVALID_INPUT);
	}

	/**
	 * Constructor with params with well defined cause.
	 * 
	 * @param what
	 *            key value/ param caused the validation error
	 * @param why
	 *            what was the cause of the exception
	 * @param badValue
	 *            the value that was validated
	 */
	public ValidationException(
	                           String what,
	                           String why,
	                           Object badValue)
	{
		msg = ErrorMessage.invalidInput(what, why, badValue);
	}

	/**
	 * Constructor from an instance of ErrorMessage.
	 * 
	 * @param msg
	 *            The ErrorMessage to initialize he exception with
	 */
	public ValidationException(
	                           ErrorMessage msg)
	{
		this.msg = msg;
	}

	/**
	 * Returns the ErrorMessage object associated with this exception.
	 * 
	 * @return reference to own copy of ErrorMessage object
	 */
	public ErrorMessage getErrorMessage()
	{
		return msg;
	}

	/**
	 * Returns the message contained in the wrapped ErrorMessage object.
	 * 
	 * @return The String message in the ErrorMessage object
	 */
	@Override
	public String toString()
	{
		return msg.getMessage();
	}

}
