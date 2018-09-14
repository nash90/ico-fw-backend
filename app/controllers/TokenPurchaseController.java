package controllers;

import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import model.Payment;
import model.PaymentTransaction;
import model.User;
import common.filter.WithModel;
import common.filter.WithValidation;
import json.PartialList;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import service.AuthorizationService;
import service.TokenPurchaseService;
import service.UserService;

@Transactional
public class TokenPurchaseController
    extends
        BaseController
{
	TokenPurchaseService						purchaseService;
	UserService									userService;
	serviceImpl.ApplicationConfigurationService	config;

	@Inject
	public TokenPurchaseController(
	                               TokenPurchaseService purchaseService,
	                               UserService userService,
	                               serviceImpl.ApplicationConfigurationService config)
	{
		this.purchaseService = purchaseService;
		this.config = config;
		this.userService = userService;
	}

	@WithModel(Payment.class)
	@WithValidation(selective = false)
	public Result pay()
	{
		AuthorizationService auth = getAuth();
		auth.isLoggedIn();
		User user = auth.getLoggedUser();

		JsonNode node = purchaseService.createTransaction(entity(), user);
		return ok(node);
	}

	public Result getPaymentTransactionByUserId(
	    long id,
	    int offset,
	    int size)
	{
		AuthorizationService auth = getAuth();
		auth.isLoggedIn();

		id = auth.getValidOwnerId(id);

		PartialList<PaymentTransaction> trans = purchaseService.getPaymentTransactionByUserId(id, offset, size);
		return ok(Json.toJson(trans));
	}

	@BodyParser.Of(BodyParser.Raw.class)
	public Result paymentCallback()
	{
		String hmac = getHmacHeader();
		String rawBody = new String(request().body()
		                                     .asBytes()
		                                     .decodeString("UTF-8"));
		purchaseService.sendIcoTokens(hmac, rawBody);
		return ok();
	}

	public Result retryWithdraw(
	    Long id)
	{
		AuthorizationService auth = getAuth();
		auth.isLoggedIn();
		PaymentTransaction txn = purchaseService.getPaymentTransactionById(id);
		auth.isValidOwner(txn.getUser());
		String result = purchaseService.retryWithdraw(txn);
		return ok(result);
	}

}
