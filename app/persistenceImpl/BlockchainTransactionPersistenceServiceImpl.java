package persistenceImpl;

import javax.inject.Inject;
import model.BlockchainTransaction;
import persistence.BlockchainTransactionPersistenceService;
import persistence.EntityManagerProvider;

public class BlockchainTransactionPersistenceServiceImpl
    extends
        JpaEntityPersistenceService<BlockchainTransaction>
    implements
        BlockchainTransactionPersistenceService
{

	@Inject
	public BlockchainTransactionPersistenceServiceImpl(
	                                                   EntityManagerProvider p)
	{
		super(p, BlockchainTransaction.class);
		// TODO Auto-generated constructor stub
	}

}
