package model;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import json.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment
    extends
        BaseEntity
{
	// --- NESTED TYPES --- //

	public static enum Currency
	{
			BTC,
			ETH,
			LTCT
	}

	private Currency currency;

	@NotNull(message = "VALIDATION_NOT_BLANK")
	private BigDecimal amount;

	@NotNull(message = "VALIDATION_NOT_BLANK")
	private BigDecimal token_amount;
}
