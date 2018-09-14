package config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import common.ErrorMessage;
import common.ServerException;
import play.Logger;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

@Singleton
public class ErrorHandler
    implements
        HttpErrorHandler
{

	public ErrorHandler()
	{
		// Nothing
	}

	@Override
	public CompletionStage<Result> onClientError(
	    RequestHeader request,
	    int statusCode,
	    String message)
	{
		// TODO Make Async
		Logger.debug("ClientError: " + request.toString());

		return CompletableFuture.completedFuture(Results.status(statusCode, "A client error occurred: " + message));
	}

	@Override
	public CompletionStage<Result> onServerError(
	    RequestHeader request,
	    Throwable exception)
	{
		// TODO Make this async
		if (exception instanceof CompletionException)
		{
			exception = exception.getCause();
		}

		// TODO Make this async
		if (exception instanceof ExecutionException)
		{
			exception = exception.getCause();
		}

		if (exception instanceof ServerException)
		{
			ErrorMessage msg = ((ServerException) exception).getErrorMessage();
			if (msg.getCode() != ErrorMessage.Code.INTERNAL)
			{
				Logger.debug(Json.prettyPrint(Json.toJson(msg)));
				return CompletableFuture.completedFuture(Results.badRequest(msg.toJson())
				                                                .as(Http.MimeTypes.JSON)
				                                                .withHeader("Access-Control-Allow-Origin", "*"));
			}
		}

		if (exception instanceof NoResultException)
		{
			Logger.debug(exception.getMessage());
			return CompletableFuture.completedFuture(Results.notFound()
			                                                .as(Http.MimeTypes.JSON)
			                                                .withHeader("Access-Control-Allow-Origin", "*"));
		}

		Logger.debug(exception.getClass()
		                      .getCanonicalName(), exception);
		return CompletableFuture.completedFuture(Results.internalServerError(ErrorMessage.internal(exception)
		                                                                                 .toJson())
		                                                .as(Http.MimeTypes.JSON)
		                                                .withHeader("Access-Control-Allow-Origin", "*"));
	}

}
