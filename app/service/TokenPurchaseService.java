package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import model.Payment;
import model.PaymentTransaction;
import model.User;
import json.PartialList;
import serviceImpl.TokenPurchaseServiceImpl;

@ImplementedBy(TokenPurchaseServiceImpl.class)
public interface TokenPurchaseService
{
	public JsonNode createTransaction(
	    Payment pay,
	    User user);

	public PaymentTransaction getPaymentTransactionById(
	    Long id);

	public PartialList<PaymentTransaction> getPaymentTransactionByUserId(
	    long id,
	    int offset,
	    int size);

	public void sendIcoTokens(
	    String hmac,
	    String rawBody);

	public String retryWithdraw(
	    PaymentTransaction txn);
}
