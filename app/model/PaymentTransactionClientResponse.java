package model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionClientResponse
{
	private BigDecimal	amount;
	private String		txn_id;
	private String		address;
	private int			confirms_needed;
	private int			timeout;
	private String		status_url;
	private String		qrcode_url;
}
