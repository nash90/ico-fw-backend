package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinpayBaseModel
{
	private double	version;
	private String	cmd;
	private String	key;
	private int		nonce;
	private String	format;
}
