package service;

import java.util.PriorityQueue;
import com.google.inject.ImplementedBy;
import model.PaymentTransaction;
import serviceImpl.PurchaseQueueServiceImpl;

@ImplementedBy(PurchaseQueueServiceImpl.class)
public interface PurchaseQueueService
{
	public PriorityQueue<PaymentTransaction> startQueue();

	public PriorityQueue<PaymentTransaction> getQueue();

	public void addToQueue(
	    PaymentTransaction txn);

	public PaymentTransaction removeFromQueue();

	public void setQueue(
	    PriorityQueue<PaymentTransaction> pq);

	public boolean removeByIdFromQueue(
	    Long id);

	public boolean checkEmptyQueue();
}
