package persistence;

import com.google.inject.ImplementedBy;
import model.PaymentTransaction;
import json.PartialList;
import persistenceImpl.PayTransactionPersistenceServiceImpl;

@ImplementedBy(PayTransactionPersistenceServiceImpl.class)
public interface PayTransactionPersistenceService
    extends
        EntityPersistenceService<PaymentTransaction>
{

	public PartialList<PaymentTransaction> getByUserId(
	    long id,
	    int offset,
	    int size);

	public PaymentTransaction getByTransactionId(
	    String txnId);

	public PartialList<PaymentTransaction> getAllPendingPayment();

}
