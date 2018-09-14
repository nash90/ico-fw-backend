package model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import json.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "blockchain_transaction")
public class BlockchainTransaction
    extends
        BaseEntity
{

	public static enum TokenStatus
	{
			NOT_PROCESSED,
			PROCESSING,
			FAILED,
			SENT,
	}

	@Column(name = "blockchain_txn_id")
	private String blockchain_txn_id;

	@Column(name = "blockchain_txn_status")
	private TokenStatus blockchain_txn_status = TokenStatus.NOT_PROCESSED;

	@Column(name = "txn_retry_count")
	private int txn_retry_count = 0;

	@Column(name = "txn_created_date")
	private Date txn_created_date;

	@Column(name = "txn_retry_date")
	private Date txn_retry_date;

	@Column(name = "payment_id")
	private Long payment_id;
}
