package model;

import java.math.BigDecimal;
import model.Payment.Currency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionClientRequest
    extends
        CoinpayBaseModel
{
	private BigDecimal	amount;
	private Currency	currency1;
	private Currency	currency2;
	private String		buyer_name;
	private String		address;
	private String		buyer_email;
	private String		item_name;
	private String		item_number;
	private String		invoice;
	private String		custom;
	private String		ipn_url;
}
