package common.filter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class PreflightAction
    extends
        Action<Preflight>
{

	// --- METHODS --- //

	@Override
	public CompletionStage<Result> call(
	    Context ctx)
	{

		if (ctx.request()
		       .method()
		       .equals("OPTIONS"))
		{
			return CompletableFuture.completedFuture(ok());
		}

		return delegate.call(ctx);
	}

}
