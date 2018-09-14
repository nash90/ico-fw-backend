package common;

/**
 * Exception Wrapper for all Authorization based Exceptions.
 */
public class AuthorizationException
    extends
        ServerException
{

	private static final long serialVersionUID = -4335476816017658042L;

	/**
	 * Constructor to initialize with a message telling the details of the cause
	 * of
	 * the exception.
	 * ErrorMessage.code will be BAD_AUTHORIZATION
	 * 
	 * @param message
	 *            The String message to initialize the exception with
	 */
	public AuthorizationException(
	                              String message)
	{
		super(ErrorMessage.badAuthoirzation(message));
	}

	/**
	 * Constructor to initialize with proper ErrorMessage class object
	 * specifying
	 * details and cause.
	 * 
	 * @param cls
	 *            Operation's target class
	 * @param op
	 *            Operation type
	 */
	public AuthorizationException(
	                              Class<?> cls,
	                              CRUD op)
	{
		super(ErrorMessage.badAuthoirzation(cls, op));
	}

}
