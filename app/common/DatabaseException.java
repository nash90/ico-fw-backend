/*--------------------------------------------------------------*\.
 |
 | Project:   m-vacation
 | Declares:  DatabaseException
 | Author(s): rafael (rafael@bluewall.jpn.com)
 |
 | Copyright (C) 2017 BlueWall Japan Inc. All rights reserved
 |
 *///////////////////////////////////////////////////////////////
package common;

public class DatabaseException
    extends
        ServerException
{

	private static final long serialVersionUID = 1L;

	public DatabaseException(
	                         String message)
	{
		super(new ErrorMessage(ErrorMessage.Code.INTERNAL, message));
	}

	public DatabaseException(
	                         Throwable cause)
	{
		super(ErrorMessage.internal(cause));
	}

	public DatabaseException(
	                         String message,
	                         Throwable cause)
	{
		super(ErrorMessage.internal(cause));
	}

}
