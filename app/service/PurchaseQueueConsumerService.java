package service;

import com.google.inject.ImplementedBy;
import serviceImpl.PurchaseQueueServiceImpl;

@ImplementedBy(PurchaseQueueServiceImpl.class)
public interface PurchaseQueueConsumerService
{
	public void startQueueConsumer();

	public void stopQueueConsumer();

	public Thread getWorker();

	public void notifyWorker();
}
