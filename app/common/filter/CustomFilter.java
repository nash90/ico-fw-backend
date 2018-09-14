package common.filter;

import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.SEE_OTHER;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.stream.Materializer;
import content.SecuredInformation;
import play.Logger;
import play.libs.Json;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import secure.Vault;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;
import common.ServerException;

public class CustomFilter
    extends
        Filter
{
	serviceImpl.ApplicationConfigurationService config;

	@Inject
	public CustomFilter(
	                    Materializer mat,
	                    serviceImpl.ApplicationConfigurationService config)
	{
		super(mat);
		this.config = config;
	}

	@Override
	public CompletionStage<Result> apply(
	    Function<Http.RequestHeader, CompletionStage<Result>> nextFilter,
	    Http.RequestHeader requestHeader)
	{
		long startTime = System.currentTimeMillis();
		String url = CONFIGURATION.INIT_REDIRECT_URL.getValue();
		String path = requestHeader.path();

		Function<String, Result> redirect = (
		    String currentUrl) -> {
			return new Result(SEE_OTHER, Collections.singletonMap(LOCATION, currentUrl));
		};

		Function<Result, Result> doLog = (
		    Result result) -> {

			String actionMethod = requestHeader.method() + "  " + requestHeader.path();
			long endTime = System.currentTimeMillis();
			long requestTime = endTime - startTime;

			Logger.info("{} took {}ms and returned {}", actionMethod, requestTime, result.status());

			return result.withHeader("Request-Time", "" + requestTime);
		};
		
		if(!Vault.getInstance().getInitializedState() && !CONFIGURATION.WALLET_KEY.getValue().equals("")) {
			ObjectNode node = JsonNodeFactory.instance.objectNode();
			node.put(SecuredInformation.EthereumPrivateKey.getKey(), CONFIGURATION.WALLET_KEY.getValue());
			try {
				Vault.getInstance().setData(Json.stringify(Json.toJson(node)));
			}catch(Exception e) {
				e.printStackTrace();
				throw new ServerException("Wallet key configuration error!!");
			}
		}
		
		if (!Vault.getInstance()
		          .getInitializedState() && !url.equals(path))
		{
			return CompletableFuture.supplyAsync(() -> {
				return redirect.apply(url);
			})
			                        .thenApply((
			                            result) -> {
				                        return doLog.apply(result);
			                        });
		} else
		{
			return nextFilter.apply(requestHeader)
			                 .thenApply((
			                     result) -> {
				                 return doLog.apply(result);
			                 });
		}

	}
}
