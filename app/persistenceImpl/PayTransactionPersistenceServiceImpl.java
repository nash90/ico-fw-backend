package persistenceImpl;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import model.BlockchainTransaction.TokenStatus;
import model.PaymentTransaction;
import model.PaymentTransaction.Status;
import json.PartialList;
import persistence.EntityManagerProvider;
import persistence.PayTransactionPersistenceService;

public class PayTransactionPersistenceServiceImpl
    extends
        JpaEntityPersistenceService<PaymentTransaction>
    implements
        PayTransactionPersistenceService
{
	@Inject
	public PayTransactionPersistenceServiceImpl(
	                                            EntityManagerProvider p)
	{
		super(p, PaymentTransaction.class);
	}

	@Override
	public PartialList<PaymentTransaction> getByUserId(
	    long id,
	    int offset,
	    int size)
	{
		return performQuery((
		    cq,
		    cb,
		    root) -> {
			cq.where(cb.equal(root.get("user")
			                      .get("id"), id));
		}, offset, size);

	}

	@Override
	public PaymentTransaction getByTransactionId(
	    String txnId)
	{
		return performSingleResultQuery((
		    cq,
		    cb,
		    root) -> {
			cq.where(cb.equal(root.get("txn_id"), txnId));
		});
	}

	@Override
	public PartialList<PaymentTransaction> getAllPendingPayment()
	{
		return performQuery((
		    cq,
		    cb,
		    root) -> {
			Predicate isNotProcessedToken = cb.equal(root.get("blockchain_txn")
			                                             .get("blockchain_txn_status"), TokenStatus.NOT_PROCESSED);
			Predicate isFailedToken = cb.equal(root.get("blockchain_txn")
			                                       .get("blockchain_txn_status"), TokenStatus.FAILED);
			Predicate isPaidPayment = cb.equal(root.get("status"), Status.PAID);
			Predicate isPendingToken = cb.or(isNotProcessedToken, isFailedToken);
			cq.where(isPaidPayment, isPendingToken);
		}, 0, -1);
	}
}
