package common;

/**
 * Wraps all Server Exceptions, caused by unexpected behaviour or conditions.
 */
public class ServerException
    extends
        Error
{

	private static final long	serialVersionUID	= -4549865330781181364L;
	private ErrorMessage		msg					= null;

	/**
	 * Constructor to initialize with proper ErrorMessage class object
	 * specifying
	 * details and cause.
	 * 
	 * @param msg
	 *            ErrorMessage object containing message and ErrorCode.
	 */
	public ServerException(
	                       ErrorMessage msg)
	{
		this.msg = msg;
	}

	public ServerException(
	                       Throwable th,
	                       ErrorMessage msg)
	{
		super(th);
		this.msg = msg;
	}

	/**
	 * Constructor to initialize with a message telling the details of the cause
	 * of the exception.
	 * 
	 * @param string
	 *            The message for the ServerException.
	 */
	public ServerException(
	                       String string)
	{
		msg = new ErrorMessage(ErrorMessage.Code.CLIENT_ERROR, string);
	}

	public ServerException(
	                       Throwable th)
	{
		super(th);
		msg = ErrorMessage.internal(th);
	}

	/**
	 * Returns the corresponding wrapped ErrorMessage.
	 * If a ErrorMessage object was not specified, Giving the exact
	 * ErrorMessage.Code INTERNAL will be returned as the code
	 * 
	 * @return The ErrorMessage object wrapped by the exception.
	 */
	public ErrorMessage getErrorMessage()
	{
		return msg;
	}

	@Override
	public String getMessage()
	{
		return msg.getMessage();
	}

	@Override
	public String getLocalizedMessage()
	{
		return msg.getMessage();
	}

}
