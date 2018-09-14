package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import model.Payment;
import model.User;
import serviceImpl.CoinPaymentServiceImpl;

@ImplementedBy(CoinPaymentServiceImpl.class)
public interface PaymentService
{
	public JsonNode createTransaction(
	    Payment pay,
	    User user);
}
