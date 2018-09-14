package model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.Payment.Currency;
import json.BaseEntity;
import json.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction
    extends
        BaseEntity
{
	// --- NESTED TYPES --- //

	public static enum Status
	{
			FAILED,
			PENDING,
			PAID
	}

	@Column(name = "payment_created_date")
	private Date payment_created_date;

	@Hidden
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "owner_id", nullable = false, updatable = true)
	private User user;

	@Column(name = "payment_amount")
	private BigDecimal amount = BigDecimal.ZERO;

	@Column(name = "payment_currency1")
	private Currency currency1;

	@Column(name = "payment_currency2")
	private Currency currency2;

	@Column(name = "payment_txn_id")
	private String txn_id;

	@Column(name = "payment_address")
	private String address;

	@Column(name = "payment_confirms_needed")
	private int confirms_needed = 0;

	@Column(name = "payment_timeout")
	private int timeout = 0;

	@Column(name = "payment_status_url")
	private String status_url;

	@Column(name = "payment_qrcode_url")
	private String qrcode_url;

	@Column(name = "payment_prebonus_token")
	private BigDecimal prebonus_token = BigDecimal.ZERO;

	@Column(name = "payment_bonus_token")
	private BigDecimal bonus_token = BigDecimal.ZERO;

	@Column(name = "payment_total_token")
	private BigDecimal total_token = BigDecimal.ZERO;

	@Column(name = "payment_txn_status")
	private Status status;

	@Hidden
	@Column(name = "payment_callback_log", length = 2048)
	private String callback_log;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "blockchain_id")
	private BlockchainTransaction blockchain_txn;

	@Column(name = "message", length = 1024)
	private String message;
}
