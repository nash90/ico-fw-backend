package serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import model.TokenBonusPhase;
import common.ServerException;
import json.PartialList;
import play.libs.Json;
import service.BlockChainService;
import service.TokenBonusService;


public class TokenBonusServiceImpl
    implements
        TokenBonusService
{
	private final static String	PHASE					= "phase";
	private final static String	START_DATE				= "startDate";
	private final static String	BONUS					= "bonus";
	private final static String	DATE_TIME_ZONE_FORMAT	= "yyyy-MM-dd HH:mm:ss XXX";

	private final static String	DATE_CANT_BE_PARSED	= "Date couldnot be parsed";
	private final static String	INVALID_DATES_ORDER	= "Dates were not configured in accending order";

	serviceImpl.ApplicationConfigurationService	config;
	BlockChainService							blockServ;

	private static PartialList<TokenBonusPhase> bonusPhases;

	@Inject
	public TokenBonusServiceImpl(
	                             serviceImpl.ApplicationConfigurationService config,
	                             BlockChainService blockServ)
	{
		super();
		this.config = config;
		this.blockServ = blockServ;

		this.initBonusPhases();
	}

	@Override
	public void setBonusPhasesFromConfig()
	{
		List<? extends Object> nodeList = config.getBonusPhase();
		List<TokenBonusPhase> phaseList = new ArrayList<TokenBonusPhase>();
		SimpleDateFormat isoFormat = new SimpleDateFormat(DATE_TIME_ZONE_FORMAT);

		for (Object obj : nodeList)
		{
			JsonNode json = Json.toJson(obj);

			int phase = json.get(PHASE)
			                .asInt();
			int bonus = json.get(BONUS)
			                .asInt();
			String startDateString = json.get(START_DATE)
			                             .asText();

			Date startDate = null;
			try
			{
				startDate = isoFormat.parse(startDateString);

			} catch (ParseException e)
			{
				throw new ServerException(DATE_CANT_BE_PARSED);
			}

			TokenBonusPhase phaseObj = new TokenBonusPhase();
			phaseObj.setPhase(phase);
			phaseObj.setBonus(bonus);
			phaseObj.setDate(startDate);

			phaseList.add(phaseObj);
		}
		validatePhaseDateOrder(phaseList);

		PartialList<TokenBonusPhase> list = new PartialList<TokenBonusPhase>(phaseList);
		TokenBonusServiceImpl.bonusPhases = list;
	}

	@Override
	public void setBonusPhasesFromSmartContract()
	{
		// TODO Auto-generated method stub

	}

	public void initBonusPhases()
	{
		String contractBonus = blockServ.getBonusInfo();
		if (contractBonus != null)
		{
			setBonusPhasesFromSmartContract();
		} else
		{
			setBonusPhasesFromConfig();
		}
	}

	@Override
	public PartialList<TokenBonusPhase> getBonusPhases()
	{
		return TokenBonusServiceImpl.bonusPhases;
	}

	private void validatePhaseDateOrder(
	    List<TokenBonusPhase> phaseList)
	{
		List<Long> dates = new ArrayList<Long>();
		for (TokenBonusPhase phase : phaseList)
		{
			dates.add(phase.getDate()
			               .getTime());
		}
		if (!dates.stream()
		          .sorted()
		          .collect(Collectors.toList())
		          .equals(dates))
		{
			throw new ServerException(INVALID_DATES_ORDER);
		}
	}
}
