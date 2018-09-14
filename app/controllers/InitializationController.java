package controllers;

import java.util.Map;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import model.TokenBonusPhase;
import json.PartialList;
import play.libs.Json;
import play.mvc.Result;
import service.InitializationService;
import service.PurchaseQueueConsumerService;
import service.TokenBonusService;

/**
 * This controller, and ONLY this controller breaks standard MVC design patterns
 * 
 * This is a special case, and it is intended to short circuit the normal
 * webside during the intitialization phase.
 * Due to the security measures requires to ensure that the keys are not kept in
 * unsecured memory, this controlle rwill handle specific authentication
 * processes that would otherwise be handles by the service part of the MVC
 * design
 * 
 */
public class InitializationController
    extends
        BaseController
{
	private static final String	CONTENT_HTML		= "text/html";
	private static final String	BONUS				= "BONUS";
	private static final String	RATES				= "RATES";
	private static final String	SETTING_NOT_FOUND	= "Setting not found";

	private final InitializationService			initializationService;
	TokenBonusService							tokenBonusService;
	PurchaseQueueConsumerService				purchaseQueueConsumer;
	serviceImpl.ApplicationConfigurationService	config;

	@Inject
	public InitializationController(
	                                InitializationService initializationService,
	                                TokenBonusService tokenBonusService,
	                                PurchaseQueueConsumerService purchaseQueueConsumer,
	                                serviceImpl.ApplicationConfigurationService config)
	{
		this.initializationService = initializationService;
		this.tokenBonusService = tokenBonusService;
		this.purchaseQueueConsumer = purchaseQueueConsumer;
		this.config = config;
	}

	public Result storeKey()
	{
		Map<String, String[]> formData = getFormBody();
		String hashedKey = this.initializationService.handleInitialization(formData);
		// Start the queue consumer after initializing key to prevent failure
		// when pending purchase is loaded from DB at startup
		purchaseQueueConsumer.startQueueConsumer();
		return ok(hashedKey);
	}

	public Result init()
	{
		this.initializationService.init();
		return ok("OK").as(CONTENT_HTML);
	}

	public Result getSettings(
	    String key)
	{
		switch (key)
		{
			case BONUS :
				// contract bonus config on init
				PartialList<TokenBonusPhase> obj = tokenBonusService.getBonusPhases();
				return ok(Json.toJson(obj));

			case RATES :
				// provide crypto rate to frontend
				JsonNode node = config.getCryptoRates();
				return ok(node);

			default :
				return ok(SETTING_NOT_FOUND);
		}
	}

}
