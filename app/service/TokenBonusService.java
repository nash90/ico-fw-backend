package service;

import com.google.inject.ImplementedBy;
import model.TokenBonusPhase;
import json.PartialList;
import serviceImpl.TokenBonusServiceImpl;

@ImplementedBy(TokenBonusServiceImpl.class)
public interface TokenBonusService
{
	public void setBonusPhasesFromConfig();

	public void setBonusPhasesFromSmartContract();

	public PartialList<TokenBonusPhase> getBonusPhases();
}
