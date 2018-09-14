package serviceImpl;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import model.PaymentTransaction;
import service.PurchaseQueueService;

public class PurchaseQueueServiceImpl
    implements
        PurchaseQueueService
{
	private static PriorityQueue<PaymentTransaction> pq;

	class PurchaseComparator
	    implements
	        Comparator<PaymentTransaction>
	{

		@Override
		public int compare(
		    PaymentTransaction task1,
		    PaymentTransaction task2)
		{
			if (task1.getBlockchain_txn()
			         .getTxn_retry_date()
			         .before(task2.getBlockchain_txn()
			                      .getTxn_retry_date()))
				return -1;
			else if (task1.getBlockchain_txn()
			              .getTxn_retry_date()
			              .after(task2.getBlockchain_txn()
			                          .getTxn_retry_date()))
				return 1;
			return 0;
		}
	}

	@Override
	public PriorityQueue<PaymentTransaction> startQueue()
	{
		PurchaseQueueServiceImpl.pq = new PriorityQueue<PaymentTransaction>(new PurchaseComparator());
		return pq;
	}

	@Override
	public PriorityQueue<PaymentTransaction> getQueue()
	{

		return PurchaseQueueServiceImpl.pq;
	}

	@Override
	public void setQueue(
	    PriorityQueue<PaymentTransaction> pq)
	{
		synchronized (PurchaseQueueServiceImpl.pq)
		{
			PurchaseQueueServiceImpl.pq = pq;
		}
	}

	@Override
	public void addToQueue(
	    PaymentTransaction txn)
	{
		synchronized (pq)
		{
			PurchaseQueueServiceImpl.pq.add(txn);
		}
	}

	@Override
	public PaymentTransaction removeFromQueue()
	{
		synchronized (pq)
		{
			return PurchaseQueueServiceImpl.pq.remove();
		}
	}

	@Override
	public boolean removeByIdFromQueue(
	    Long id)
	{

		synchronized (pq)
		{
			return pq.removeIf(equalsTxnId(id));
		}

	}

	public static Predicate<? super PaymentTransaction> equalsTxnId(
	    Long id)
	{
		return p -> p.getId()
		             .equals(id);
	}

	@Override
	public boolean checkEmptyQueue()
	{
		synchronized (pq)
		{
			return pq.isEmpty();
		}
	}
}
