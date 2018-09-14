package persistence;

import com.google.inject.ImplementedBy;
import model.BlockchainTransaction;
import persistenceImpl.BlockchainTransactionPersistenceServiceImpl;

@ImplementedBy(BlockchainTransactionPersistenceServiceImpl.class)
public interface BlockchainTransactionPersistenceService
    extends
        EntityPersistenceService<BlockchainTransaction>
{

}
